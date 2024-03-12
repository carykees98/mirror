#include <config/config.hpp>
#include <scheduler/module.hpp>

#include <iostream>

namespace mirror {
    DispatchConfig* DispatchConfig::instance = nullptr;

    std::mutex DispatchConfig::access;

    DispatchConfig* DispatchConfig::getInstance() {
        std::lock_guard<std::mutex> guard(access);
        if(instance == nullptr) { instance = new DispatchConfig(); }
        return instance;
    }

    DispatchConfig::DispatchConfig() {
        this->logger_host = "localhost";
        this->logger_port = 4357;

        this->modules.push_back(new DispatchModule(
            std::string("mirror-api"), 
            std::string("localhost"), 
            10001
        ));
        this->modules.push_back(new DispatchModule(
            std::string("mirror-log"), 
            std::string("localhost"), 
            10002
        ));
    }

    DispatchConfig::~DispatchConfig() {
        for(int i = 0; i < this->modules.size(); i++) { delete modules.at(i); }
    }

    void DispatchConfig::print(unsigned int indent) {
        std::string sp(' ', indent);
        
        std::cout << sp << "DispatchConfig\n";

        std::cout << sp << "  logger_port: ";
        std::cout << this->logger_port << "\n";

        std::cout << sp << "  logger_host: ";
        std::cout << this->logger_host << "\n";

        std::cout << sp << "  modules:\n";
        for(int i = 0; i < modules.size(); i++) {
            modules.at(i)->print(indent + 4);
        }
    }
}