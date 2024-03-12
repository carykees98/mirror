#pragma once

#include <string>
#include <scheduler/status.hpp>

namespace mirror {
    /**
     * DispatchModule represents a service that Dispatch controls. 
     */
    class DispatchModule {
    public:
        /**
         * Constructs a DispatchModule with a given name and IP address 
         */
        DispatchModule(std::string name, std::string host, uint16_t port);

        /**
         * Destroys this DispatchModule
         */
        ~DispatchModule();

        /**
         * Gets this DispatchModule's name 
         */
        std::string getName() { return name; }

        /**
         * Gets this DispatchModule's host
         */
        std::string getHost() { return host; }

        /**
         * Gets this DispatchModule's status  
         */
        mirror::Status& getStatus() { return status; }

        /**
         * Sets this DispatchModule's status
         */
        void setStatus(mirror::Status& status) { this->status = status; }
    
        void print(unsigned int indent);

        void print() { this->print(0); }
    private:
        std::string name;
        std::string host;
        uint16_t port;
        mirror::Status status;
    };
}