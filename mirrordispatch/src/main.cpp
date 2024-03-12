#include <io/docker.hpp>
#include <scheduler/scheduler.hpp>
#include <scheduler/module.hpp>
#include <scheduler/status.hpp>
#include <scheduler/job.hpp>
#include <config/config.hpp>
#include <mirror/logger.hpp>
#include <tests.hpp>
#include <io/log.hpp>

#include <iostream>
#include <cstring>
#include <csignal>
#include <chrono>

using namespace mirror;

void terminate(int signal) {
    std::cout << "Terminated!";
    DispatchScheduler* scheduler = DispatchScheduler::getInstance();
    scheduler->interrupt();
    Logger* logger = Logger::getInstance();
    logger->close();
}

int main(int argc, char* argv[]) {
    if(argc == 2 && strcmp(argv[1], "--test") == 0) { return Tests::run(); }
    std::cout << "MirrorDispatch" << std::endl;

    std::signal(SIGINT, terminate);

    Logger* logger = Logger::getInstance();
    logger->configure(4357, std::string("test"), std::string("localhost"));

    DispatchConfig* config = DispatchConfig::getInstance();
    DispatchScheduler* scheduler = DispatchScheduler::getInstance();
    std::cout << "Got instance\n";

    std::cout << "Setting up Docker\n";
    Docker* docker = Docker::getInstance();

    DispatchModule a = DispatchModule(std::string("test1"), std::string("host1"), 1234);
    //docker->startModule(a);
    docker->getStatus(a);

    DispatchLog log("test28356");
    log.info("test");

    DispatchLog log2("test");
    log2.info("test");
    int ignored;
    //std::cin >> ignored;
}
