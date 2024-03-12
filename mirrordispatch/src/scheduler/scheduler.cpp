#include <mirror/logger.hpp>
#include <io/docker.hpp>
#include <scheduler/job.hpp>
#include <config/config.hpp>

#include <mutex>
#include <memory>
#include <list>
#include <queue>
#include <iostream>
#include <thread>

#include <scheduler/scheduler.hpp>

namespace mirror {

    DispatchScheduler* DispatchScheduler::instance = nullptr;

    std::mutex DispatchScheduler::access;

    DispatchScheduler* DispatchScheduler::getInstance() {
        std::lock_guard<std::mutex> guard(access);
        if(instance == nullptr) { instance = new DispatchScheduler(); }
        return instance;
    }

    DispatchScheduler::DispatchScheduler() :
        jobs(std::queue<DispatchJob>()),
        docker(Docker::getInstance()),
        net_log(Logger::getInstance()),
        config(DispatchConfig::getInstance()),
        scheduler_thread(std::thread(runScheduler)),
        log(DispatchLog("Scheduler")),
        flag_interrupt(false)
    {}

    void DispatchScheduler::scheduleStart() {
        log.info("Scheduled start.");
    }

    void DispatchScheduler::scheduleRestart() {
        log.info("Scheduled restart.");
    }

    void DispatchScheduler::scheduleShutdown() {
        log.info("Scheduled shutdown.");
    }

    void DispatchScheduler::scheduleRefresh() {
        log.info("Scheduled refresh.");
    }

    void DispatchScheduler::print(unsigned int indent) {
        std::vector<DispatchModule*> modules = config->getModules();
        std::string sp(' ', indent);
        std::cout << sp << "DispatchScheduler:\n";
        std::cout << sp << "  Modules:\n";
        for(int i = 0; i < modules.size(); i++) {
            modules.at(i)->print(indent + 4);
        }
    }

    void DispatchScheduler::pushJob(DispatchJob& job) {
        std::lock_guard<std::mutex> guard(access);
        jobs.push(job);
    }

    DispatchJob DispatchScheduler::nextJob() {
        std::lock_guard<std::mutex> guard(access);
        return jobs.front();
    }

    void DispatchScheduler::popJob() {
        std::lock_guard<std::mutex> guard(access);
        jobs.pop();
    }

    void DispatchScheduler::runScheduler() {
        std::cout << "Scheduler thread started.\n";
        DispatchScheduler* scheduler = DispatchScheduler::getInstance();
        while(true) {
            std::this_thread::sleep_for(std::chrono::milliseconds(500));
            std::cout << "Doing something.\n";
            if(scheduler->isInterrupted()) {
                std::cout << "Interrupted.\n";
                break;
            }
        }
        std::cout << "Done\n";
    }

    void DispatchScheduler::interrupt() {
        std::cout << "Interrupted!!!!\n";
        flag_interrupt = true;
        
        scheduler_thread.join();
        //scheduler_thread.~thread();
        std::cout << "Joined\n";
        
    }
}