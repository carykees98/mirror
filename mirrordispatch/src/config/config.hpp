#pragma once

#include <scheduler/module.hpp>

#include <string>
#include <mutex>
#include <vector>

namespace mirror {
    /**
     * DispatchConfig allows classes to access application properties that are
     * loaded from a config file.
     */
    class DispatchConfig {
    public:
        /**
         * Deleted constructors 
         */
        DispatchConfig(DispatchConfig &) = delete;
        DispatchConfig(DispatchConfig &&) = delete;
        DispatchConfig &operator=(const DispatchConfig &) = delete;
        DispatchConfig &operator=(const DispatchConfig &&) = delete;

        /**
         * Gets the instance of DispatchConfig. If it does not already exist,
         * it will be created.
         */
        static DispatchConfig* getInstance();

        /**
         * Gets the hostname of the log server.
         * @return The log server's hostname
         */
        std::string& getLoggerHost() { return logger_host; }

        /**
         * Gets the port of the log server.
         * @return The log server's port 
         */
        uint16_t getLoggerPort() { return logger_port; }

        std::vector<DispatchModule*>& getModules() { return modules; }

        /**
         * Prints this DispatchConfig to stdout with the specified indent.
         * @param indent The indent to print with 
         */
        void print(unsigned int indent);

        /**
         * Prints this DispatchConfig to stdout.
         */
        void print() { this->print(0); }
    
    protected:
        DispatchConfig();

        ~DispatchConfig();

    private:
        static DispatchConfig* instance;
        static std::mutex access;
        std::string logger_host;
        uint16_t logger_port;
        std::vector<DispatchModule*> modules;
    };
}