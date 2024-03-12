#include <scheduler/status.hpp>

#include <string>
#include <iostream>

#include <scheduler/module.hpp>

namespace mirror {
    DispatchModule::DispatchModule(std::string name, 
            std::string host, uint16_t port) :
        name(name),
        host(host),
        status(Status::States::UNKNOWN)
    {}


    DispatchModule::~DispatchModule() {}

    void DispatchModule::print(unsigned int indent) {
        std::string sp(indent, ' ');
        std::cout << sp << "DispatchModule:\n";
        std::cout << sp << "  name: \"" << this->name << "\"\n";
        std::cout << sp << "  host: \"" << this->host << "\"\n";
        std::cout << sp << "  status:\n";
        this->status.print(indent + 4);
    }

}