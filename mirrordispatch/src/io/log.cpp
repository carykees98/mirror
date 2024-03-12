#include <iostream>
#include <chrono>
#include <iomanip>

#include <io/log.hpp>

namespace mirror {
    void DispatchLog::put(uint8_t level, std::string& message) {
        std::time_t now = std::chrono::system_clock::to_time_t(
            std::chrono::system_clock::now()
        );
        out << std::put_time(std::localtime(&now), "%F %T ");
        switch(level) {
            case 1:
                out << "[ INFO ]  ";
                break;
            case 2:
                out << "[ WARN ]  ";
                break;
            case 3:
                out << "[ ERROR ] ";
                break;
            case 4:
                out << "[ FATAL ] ";
                break;
            default:
                out << "[ DEBUG ] ";
        }
        out << std::left << std::setw(24) << name << ": " << message << "\n";
    }

    void DispatchLog::put(uint8_t level, const char* message) {
        std::string m_str = std::string(message);
        put(level, m_str);
    }
}