#include <event/timestamp.hpp>
#include <sstream>
#include <iomanip>

namespace mirror {

    /* ----- Public ----- */

    TimeStamp::TimeStamp(const std::string &line) {
        uint8_t index = 0;
        this->day = parseNumber<uint8_t>(line, index);
        this->month = parseMonth(line, index);
        this->year = parseNumber<uint16_t>(line, index);
        this->hour = parseNumber<uint8_t>(line, index);
        this->minute = parseNumber<uint8_t>(line, index);
        this->second = parseNumber<uint8_t>(line, index);
    }

    std::string TimeStamp::toString() {
        std::stringstream s;
        s << std::setfill('0') << std::setw(4) << (uint) year << "-";
        s << std::setfill('0') << std::setw(2) << (uint) month << "-";
        s << std::setfill('0') << std::setw(2) << (uint) day << " ";
        s << std::setfill('0') << std::setw(2) << (uint) hour << ":";
        s << std::setfill('0') << std::setw(2) << (uint) minute << ":";
        s << std::setfill('0') << std::setw(2) << (uint) second;
        return s.str();
    }

    /* ----- Private ----- */

    uint8_t TimeStamp::parseMonth(const std::string &line, uint8_t &index) {
        std::stringstream s;
        for(; index < line.size() && !isSeparator(line.at(index)); index++) {
            s << line.at(index);
        }
        index++;

        try {
            return month_map.at(s.str());
        } catch(const std::out_of_range& e) {
            return 0;
        }
    }

    template <typename T>
    T TimeStamp::parseNumber(const std::string &line, uint8_t &index) {
        std::stringstream s;
        for(; index < line.size() && !isSeparator(line.at(index)); index++) {
            s << line.at(index);
        }
        index++;
        uint16_t result = 0;
        s >> result;
        return result;
    }
    
    inline bool TimeStamp::isSeparator(const char& c) {
        return c == '/' || c == ':' || c == ' ';
    }

    const std::map<std::string, uint8_t> TimeStamp::month_map {
            {"Jan", 1}, {"Feb", 2},  {"Mar", 3},  {"Apr", 4}, 
            {"May", 5}, {"Jun", 6},  {"Jul", 7},  {"Aug", 8}, 
            {"Sep", 9}, {"Oct", 10}, {"Nov", 11}, {"Dec", 12},
    };
}
