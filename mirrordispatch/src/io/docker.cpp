#include <io/log.hpp>

#include <mutex>
#include <sstream>
#include <iostream>

#include <io/docker.hpp>

namespace mirror {
    
    Docker* Docker::instance = nullptr;
    std::mutex Docker::access;

    /**
     * Public 
     */

    Docker* Docker::getInstance() {
        std::lock_guard<std::mutex> lock(access);
        if(instance == nullptr) { instance = new Docker(); }
        return instance;
    }

    bool Docker::startModule(DispatchModule& module) {
        std::lock_guard<std::mutex> lock(access);
        log.info(
            std::string("Starting module \"" + module.getName() + "\"...")
        );
        std::stringstream command;
        command << "docker start " << module.getName();
        log.debug(
            std::string("Running command: \"" + command.str() + "\"...")
        );
        std::string result = runCommand(command.str());
        log.debug(
            std::string("Command result: \"" + result + "\"...")
        );
        return true;
    }

    bool Docker::stopModule(DispatchModule& module) {
        std::lock_guard<std::mutex> lock(access);
        log.info(
            std::string("Stopping module \"" + module.getName() + "\"...")
        );
        std::stringstream command;
        command << "docker stop " << module.getName();
        log.debug(
            std::string("Running command: \"" + command.str() + "\"...")
        );
        std::string result = runCommand(command.str());
        log.debug(
            std::string("Command result: \"" + result + "\"...")
        );
        return true;
    }

    bool Docker::restartModule(DispatchModule& module) {
        std::lock_guard<std::mutex> lock(access);
        log.info(
            std::string("Restarting module \"" + module.getName() + "\"...")
        );
        std::stringstream command;
        command << "docker restart " << module.getName();
        log.debug(
            std::string("Running command: \"" + command.str() + "\"...")
        );
        std::string result = runCommand(command.str());
        log.debug(
            std::string("Command result: \"" + result + "\"...")
        );
        return true;
    }

    bool Docker::getStatus(DispatchModule& module) {
        std::lock_guard<std::mutex> lock(access);
        log.info(
            std::string("Getting status of \"" + module.getName() + "\"...")
        );
        std::stringstream command;
        command << "docker stats --no-stream " << module.getName();
        log.debug(
            std::string("Running command: \"" + command.str() + "\"...")
        );
        std::string result = runCommand(command.str());
        log.debug(
            std::string("Command result: \"" + result + "\"...")
        );
        return true;
    }

    /**
     * Protected 
     */

    Docker::Docker() :
        log(DispatchLog("docker"))
    {
        log.debug("Instantiated Docker.");
    }

    Docker::~Docker() {}

    /**
     * Private
     */

    std::string Docker::runCommand(std::string command) {
        std::string output = "";
        FILE* pipe = popen(command.c_str(), "r");
        if(!pipe) {
            std::cout << "Couldn't open pipe!\n";
            return nullptr;
        }
        try {
            char buffer[512];
            while(fgets(buffer, 512, pipe) != NULL) {
                output += buffer;
            }
        } catch(...) { 
            std::cout << "Error reading from stream!\n";
            output = nullptr;
        }
        pclose(pipe);
        return output;
    }
}