#include <string>
#include <map>

#include <mirror/logger.hpp>

namespace mirror {
    class State {
    public:
        /**
         * Constructs a State. Will first attempt to load application state 
         * from the disk, if this fails it will create a new, empty state.
        */
        State();

        /**
         * @returns Last seen line from the NGINX log, or "" if none exists.
        */
        const std::string &getLastEvent() { return last_event; }

        /**
         * Registers the last event (line of the NGINX log) that we have seen
         * @param event Last line of the NGINX log that we have seen
        */
        void registerLastEvent(const std::string &event) { last_event = event; }

        /**
         * Saves application state to disk.
        */
        void save();

    private:
        std::string last_event;

        Logger *logger;
    
    };

}
