#pragma once

#include <chrono>

namespace mirror {
    /**
     * Status represents a limited-lifetime status of a single module.
     * Upon the expiration of a status, the scheduler will attempt to get a
     * new status.
     */
    class Status {
    public:
        /**
         * The possible module states a Status can have.
         * UP - This module is working properly.
         * WARN - Something is wrong, but we aren't going to do anything yet.
         * DOWN - This module is not working.
         * UNKNOWN - We couldn't get the status of this module 
         */
        enum class States {
            UP,
            WARN,
            DOWN,
            UNKNOWN
        };

        /**
         * Constructs a Status with a given state.
         * @param state The state (UP, DOWN, WARN, UNKNOWN) of the module
         */
        Status(Status::States state);

        /**
         * Constructs a Status with state States::UNKNOWN.
         */
        Status();

        /**
         * Get this Status's state.
         * @return this Status's state
         */
        States getState() { return state; };

        /**
         * Is this Status expired?
         * @return true if this Status is expired 
         */
        bool isExpired();

        /**
         * Prints this Status to stdout with the specified indent.
         * @param indent Indent to print with 
         */
        void print(unsigned int indent);
        
        /**
         * Prints this Status to stdout.
         */
        void print() { this->print(0); }
    
    private:
        States state;
        std::chrono::system_clock::time_point creation;
        std::chrono::system_clock::time_point expiry;
    };
}