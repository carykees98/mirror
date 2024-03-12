#pragma once

#include <io/log.hpp>
#include <scheduler/module.hpp>

#include <mutex>

namespace mirror {
    /**
     * Docker provides functionality for starting, stopping, and checking
     * the status of containers, and synchronizes doing so.
     */
    class Docker {
    public:
        /**
         * Deleted constructors 
         */
        Docker(Docker &) = delete;
        Docker(Docker &&) = delete;
        Docker &operator=(const Docker &) = delete;
        Docker &operator=(const Docker &&) = delete;

        /**
         * Gets the instance of Docker. Will create it if it does not already
         * exist.
         * @return Pointer to instance of Docker
         */
        static Docker* getInstance();

        /**
         * Starts a module.
         * @param module Module to start
         * @returns True if starting the module succeeds 
         */
        bool startModule(DispatchModule& module);

        /**
         * Stops a module.
         * @param module Module to stop
         * @returns True if stopping the module succeeds 
         */
        bool stopModule(DispatchModule& module);

        /**
         * Restarts a module.
         * @param module Module to restart
         * @returns True if restarting the module succeeds 
         */
        bool restartModule(DispatchModule& module);

        /**
         * Gets the status of a module (Checks if it is running)
         * @param module Module to check the status of
         * @return True if the given module is running
         */
        bool getStatus(DispatchModule& module);

    protected:
        Docker();

        ~Docker();
    
    private:
        static Docker* instance;
        static std::mutex access;
        DispatchLog log;

    private:
        /**
         * Runs a command and returns its output.
         * Will only capture stdout.
         * If an error occurs, will return NULL. 
         */
        static std::string runCommand(std::string command);
    };
}