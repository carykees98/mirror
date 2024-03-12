#pragma once

#include <iostream>


namespace mirror {
    /**
     * DispatchLog provides functionality 
     */
    class DispatchLog {
    public:
        /**
         * Constructs a DispatchLog.
         * @param module_name Module name to prepend log events with
         */
        DispatchLog(const char* module_name) :
            name(std::string(module_name)), 
            out(std::cout) 
        {}

        /**
         * Constructs a DispatchLog.
         * @param module_name Module name to prepend log events with
         * @param out_stream Output stream to print events to
         */
        DispatchLog(const char* module_name, std::ostream& out_stream) :
            name(std::string(module_name)), 
            out(out_stream) 
        {}

        /**
         * Prints a log event with severity "DEBUG"
         * @param message Message to log
         */
        void debug(std::string& message) { this->put(0, message); }
        void debug(std::string message) { this->put(0, message); }
        void debug(const char* message) { this->put(0, message); }

        /**
         * Prints a log event with severity "INFO"
         * @param message Message to log
         */
        void info(std::string& message) { this->put(1, message); }
        void info(std::string message) { this->put(1, message); }
        void info(const char* message) { this->put(1, message); }

        /**
         * Prints a log event with severity "WARN"
         * @param message Message to log
         */
        void warn(std::string& message) { this->put(2, message); }
        void warn(std::string message) { this->put(2, message); }
        void warn(const char* message) { this->put(2, message); }

        /**
         * Prints a log event with severity "ERROR"
         * @param message Message to log
         */
        void error(std::string& message) { this->put(3, message); }
        void error(std::string message) { this->put(3, message); }
        void error(const char* message) { this->put(3, message); }

        /**
         * Prints a log event with severity "FATAL"
         * @param message Message to log
         */
        void fatal(std::string& message) { this->put(4, message); }
        void fatal(std::string message) { this->put(4, message); }
        void fatal(const char* message) { this->put(4, message); }

    private:
        std::string name;
        std::ostream& out;
    
    private:
        /**
         * Prints a log event.
         * @param level (0-4) Severity of the log event
         * @param message Message for the log event
         */
        void put(uint8_t level, std::string& message);
        void put(uint8_t level, const char* message);
    };
}