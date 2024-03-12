#pragma once
#include <string>
#include <event/timestamp.hpp>

namespace mirror {
    /**
     * Event stores the data from a single NGINX log event.
    */
    class Event {
    public: // methods
        /**
         * Constructs an Event by parsing a line from the NGINX log.
         * Note: The parser assumes that NGINX escapes double quotes in its log
         * output. This is its default behavior, but the parser may behave
         * unexpectedly if this is not the case.
         * @param line Line from the NGINX log to parse
        */
        Event(const std::string &line);

        /**
         * Destroys this Event.
        */
        ~Event() { delete time_stamp; }

        /**
         * @returns This Event as a JSON string
        */
        std::string toString();

        /**
         * @returns This Event's status
        */
        uint16_t getStatus() { return status; }

        /**
         * @returns This Event's bytes sent
        */
        uint64_t getBytesSent() { return bytes_sent; }

        /**
         * @returns This Event's project (or an empty string if none is found)
        */
        const std::string &getProject() { return project; }

        /**
         * @returns This Event's path (or an empty string if none is found)
        */
        const std::string &getPath() { return path; }

    public: // operators
        friend bool operator<(const Event &l, const Event &r) {
            return *l.time_stamp < *r.time_stamp;
        }

        friend bool operator>(const Event &l, const Event &r) {
            return *l.time_stamp > *r.time_stamp;
        }

        friend bool operator==(const Event &l, const Event &r) {
            return     *l.time_stamp == *r.time_stamp 
                    && l.remote_addr == r.remote_addr
                    && l.project     == r.project
                    && l.path        == r.path
                    && l.status      == r.status
                    && l.bytes_sent  == r.bytes_sent
                    && l.bytes_recv  == r.bytes_recv
                    && l.user_agent  == r.user_agent;
        }

    private: // data
        TimeStamp *time_stamp;

        std::string remote_addr;

        std::string project;

        std::string path;

        uint16_t status;

        uint64_t bytes_sent;

        uint64_t bytes_recv;

        std::string user_agent;

    private: // methods
        /**
         * Returns the next string (enclosed with '"') in the line.
         * This function assumes that instances of '"' within the strings
         * themselves are escaped.
         * @param line Line to scan
         * @param index Index to start scanning at
         * @returns The next '"'-enclosed string in the line.
        */
        std::string nextString(const std::string &line, uint32_t &index);

        /**
         * Returns true if the character at {index} is '"' and either of the 
         * following are true:
         *  1. {index} is 0.
         *  2. The character at {index - 1} is '\'.
         * @param index Index of the character to test
         * @returns True if the character at the given index is an unescaped '"'
        */
        inline bool isQuote(const std::string &line, uint32_t &index);

        /**
         * Returns the next number (enclosed with '"') in the line.
         * This function assumes that instances of '"' within the strings
         * themselves are escaped.
         * @param line Line to scan
         * @param index Index to start scanning at
         * @returns The next '"'-enclosed number in the line.
        */
        template <typename T>
        T nextNumber(const std::string &line, uint32_t &index);

        /**
         * Grabs the path from a request
         * Note: Only ran if a request returns 2xx to filter malicious requests.
         * @param request Request to parse
         * @returns The path in the request, or an empty string if it is invalid
        */
        std::string parsePath(const std::string &request);

        /**
         * Grabs the project name from a path
         * Note: Only ran if a request returns 2xx to filter malicious requests.
         * @param path Path
         * @returns The project name in the path, or an empty string if it is invalid
        */
        std::string parseProject(const std::string &path);
    };
}
