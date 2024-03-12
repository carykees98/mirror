#include <string>
#include <chrono>
#include <iostream>

#include <scheduler/status.hpp>

namespace mirror {

    /**
     * Public 
     */
    Status::Status(Status::States state) :
        state(state),
        creation(std::chrono::system_clock::now()),
        expiry(creation + std::chrono::minutes(1))
    {}

    Status::Status() : Status(States::UNKNOWN) {}

    bool Status::isExpired() {
        return std::chrono::system_clock::now() > expiry;
    }

    void Status::print(unsigned int indent) {
        std::string sp(indent, ' ');
        std::cout << sp << "Status:\n";

        std::cout << sp << "  state: ";
        switch(this->state) {
            case States::UP:
                std::cout << "UP";
                break;
            case States::WARN:
                std::cout << "WARN";
                break;
            case States::DOWN:
                std::cout << "DOWN";
                break;
            default:
                std::cout << "UNKNOWN";
                break;
        }
        std::cout << "\n";

        std::cout << sp << "  creation: ";
        std::cout << std::chrono::system_clock::to_time_t(this->creation) << "\n";

        std::cout << sp << "  expiry: ";
        std::cout << std::chrono::system_clock::to_time_t(this->expiry) << "\n";

        std::cout << sp << "  isExpired: ";
        std::cout << this->isExpired() << "\n";
    }

    /**
     * Private
     */
}