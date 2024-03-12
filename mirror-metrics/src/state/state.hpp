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
         * @returns Map of project name to number of hits
        */
        const std::map<std::string, u_long> &getHits() { return hits; }

        /**
         * @returns Map of project name to bytes sent
        */
        const std::map<std::string, u_long> &getBytesSent() { return bytes_sent; }

        /**
         * @returns Last seen line from the NGINX log, or "" if none exists.
        */
        const std::string &getLastEvent() { return last_event; }

        /**
         * Registers a hit for the given project.
         * @param project Project name to register a hit for
        */
        void registerHit(const std::string &project);
        
        /**
         * Registers bytes sent for the given project
         * @param project Project name to register bytes sent for
        */
        void registerBytesSent(const std::string &project, uint64_t req_bytes_sent);

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
        std::map<std::string, u_long> hits;

        std::map<std::string, u_long> bytes_sent;

        std::string last_event;

        Logger *logger;
    
    };

}
