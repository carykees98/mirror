#include <event/event.hpp>
#include <sstream>

namespace mirror {

    /* ----- Public ----- */

    Event::Event(const std::string &line) {
        uint32_t index = 0;
        std::string timestamp_raw = nextString(line, index);
        this->time_stamp = new TimeStamp(timestamp_raw);
        this->remote_addr = nextString(line, index);
        std::string request = nextString(line, index);
        this->status = nextNumber<uint16_t>(line, index);
        this->bytes_sent = nextNumber<uint16_t>(line, index);
        this->bytes_recv = nextNumber<uint16_t>(line, index);
        this->user_agent = nextString(line, index);

        // do not parse path or project for potentially malicious requests
        if(this->status >= 200 && this->status < 300) {
            this->path = parsePath(request);
            this->project = parseProject(this->path);
        } else {
            this->path = "";
            this->project = "";
        }
    }

    std::string Event::toString() {
        std::stringstream s;
        s << "{\"time_stamp\":\"" << time_stamp->toString() << "\",";
        s << "\"remote_addr\":\"" << remote_addr << "\",";
        s << "\"path\":\"" << path << "\",";
        s << "\"status\":" << status << ",";
        s << "\"bytes_sent\":" << bytes_sent << ",";
        s << "\"bytes_recv\":" << bytes_recv << ",";
        s << "\"user_agent\":\"" << user_agent << "\"}";

        return s.str();
    }



    /* ----- Private ----- */

    std::string Event::nextString(const std::string &line, uint32_t &index) {
        std::stringstream s;
        // skip to next string
        for(; index < line.size() && !isQuote(line, index); index++);
        index++; // skip opening "
        for(; index < line.size() && !isQuote(line, index); index++) {
            s << line.at(index);
        }
        index++; // skip closing "
        return s.str();
    }

    template <typename T>
    T Event::nextNumber(const std::string &line, uint32_t &index) {
        std::stringstream s;
        T result;
        // skip to next string
        for(; index < line.size() && !isQuote(line, index); index++);
        index++; // skip opening "
        for(; index < line.size() && !isQuote(line, index); index++) {
            s << line.at(index);
        }
        index++; // skip closing "
        s >> result;
        return result;
    }

    inline bool Event::isQuote(const std::string &line, uint32_t &index) {
        return line.at(index) == '"' 
                && (index == 0 || line.at(index - 1) != '\\');
    }

    std::string Event::parsePath(const std::string &request) {
        if(request.size() <= 1) { return ""; }
        uint16_t begin = 0;
        uint16_t end = request.size() - 1;
        for(; begin < request.size() && request.at(begin) != ' '; begin++);
        begin++; // skip space
        if(begin >= request.size()) { return ""; }
        for(; end >= 0 && request.at(end) != ' '; end--);
        if(end <= begin) { return ""; }
        return request.substr(begin, end - begin);
    }

    std::string Event::parseProject(const std::string &path) {
        std::stringstream s;
        for(uint16_t i = 1; i < path.size() && path.at(i) != '/'; i++) {
            s << path.at(i);
        }
        return s.str();
    }
}
