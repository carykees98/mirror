#pragma once

#include <io/docker.hpp>
#include <scheduler/job.hpp>
#include <mirror/logger.hpp>
#include <config/config.hpp>
#include <queue>

namespace mirror {
    /**
     * DispatchScheduler creates, queues, and publishes Jobs. 
     */
    class DispatchScheduler {
    public:
        /**
         * Deleted constructors
         */
        DispatchScheduler(DispatchScheduler &) = delete;
        DispatchScheduler(DispatchScheduler &&) = delete;
        DispatchScheduler &operator=(const DispatchScheduler &) = delete;
        DispatchScheduler &operator=(const DispatchScheduler &&) = delete;

        /**
         * Gets the instance of DispatchScheduler. If there is none, it will
         * construct it.
         * @returns Pointer to instance of DispatchScheduler
         */
        static DispatchScheduler* getInstance();

        /**
         * Schedule startup of a module
         */
        void scheduleStart();

        /**
         * Schedule restart of a module
         */
        void scheduleRestart();

        /**
         * Schedule shutdown of a module
         */
        void scheduleShutdown();

        /**
         * Schedule a refresh of a status  
         */
        void scheduleRefresh();

        /**
         * Prints this Scheduler to stdout with the specified indent
         */
        void print(unsigned int indent);

        /**
         * Prints this Scheduler to stdout
         */
        void print() { this->print(0); }

        /**
         * Adds a job to the queue.
         * @param job Job to add to the queue 
         */
        void pushJob(DispatchJob& job);

        /**
         * Gets the next job in the queue.
         * @returns The next job in the queue
         */
        DispatchJob nextJob();

        /**
         * Pops a job off of the queue. 
         */
        void popJob();
        
        /**
         * Interrupts the scheduler thread.
         */
        void interrupt();

        /**
         * Closes the DispatchScheduler. 
         */
        void close();

    protected:
        DispatchScheduler();

        ~DispatchScheduler();
        
        /**
         * Starts the scheduler thread. 
         */
        static void runScheduler();

        /**
         * Checks if the scheduler thread is interrupted. 
         */
        bool isInterrupted() { return flag_interrupt; }

    private:
        static DispatchScheduler* instance;
        static std::mutex access;
        Docker* docker;
        Logger* net_log;
        DispatchConfig* config;
        DispatchLog log;
        std::queue<DispatchJob> jobs;
        std::thread scheduler_thread;
        bool flag_interrupt;        
    };
}