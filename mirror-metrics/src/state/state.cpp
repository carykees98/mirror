#include <state/state.hpp>

#include <nlohmann/json.hpp>

#include <iostream>
#include <fstream>
#include <string>
#include <map>
#include <filesystem>

using json = nlohmann::json;

#define FILE_PATH "data/mirror-metrics-state.json"
#define BACKUP_FILE_PATH "data/mirror-metrics-state.json.bak"

namespace mirror {

    /* ----- Public ----- */

    State::State() {
        this->logger = Logger::getInstance();
        logger->info("Creating application state...");
        std::ifstream f;
        f.open(FILE_PATH);
        if(f.fail()) {
            logger->warn("Failed to load application state from disk.");
            this->hits = std::map<std::string, u_long>();
            this->bytes_sent = std::map<std::string, u_long>();
            this->last_event = "";
            return;
        }
        json state = json::parse(f);
        state.items();
        f.close();
        this->hits = state["hits"].template get<std::map<std::string, u_long>>();
        this->bytes_sent = state["bytes_sent"].template get<std::map<std::string, u_long>>();
        this->last_event = state["last_event"].template get<std::string>();
    }

    void State::save() {
        logger->debug("Saving application state...");
        json j;
        j["hits"] = hits;
        j["bytes_sent"] = bytes_sent;
        j["last_event"] = last_event;
        std::ofstream f;
        std::rename(FILE_PATH, BACKUP_FILE_PATH);
        f.open(FILE_PATH, std::ofstream::trunc);
        if(f.fail()) {
            logger->error("Failed to save application state.");
        } else {
            f << j;
            f.close();
        }
        std::stringstream s;
        s << "Saved state: \"" << j << "\"";
        logger->debug(s.str());   
    }

    void State::registerHit(const std::string &project) {
        hits.insert({project, 0});
        hits[project] += 1;
    }

    void State::registerBytesSent(const std::string &project, uint64_t req_bytes_sent) {
        bytes_sent.insert({project, 0});
        bytes_sent[project] += req_bytes_sent;
    }
    
}
