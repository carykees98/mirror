#include <event/event.hpp>
#include <event/timestamp.hpp>
#include <mirror/logger.hpp>
#include <state/state.hpp>

#include <prometheus/counter.h>
#include <prometheus/exposer.h>
#include <prometheus/registry.h>

#include <memory>
#include <iostream>
#include <thread>
#include <csignal>

#include <zmq.hpp>

/**
 * The interval at which counters' state is saved to the disk. (Seconds)
 * 1.0 is a good default.
*/
#define STATE_UPDATE_INTERVAL 1.0

#define LOG_SERVER_ADDRESS "mirrorlog"

#define LOG_SERVER_PORT 4001

using namespace mirror;

int main() {
    // Connect to log server
    Logger* logger = Logger::getInstance();
    logger->configure(LOG_SERVER_PORT, "Metrics Engine", LOG_SERVER_ADDRESS);

    // Create publisher socket
    zmq::context_t socketContext(1, 1);
    zmq::socket_t pubSocket{socketContext, zmq::socket_type::pub};
    pubSocket.bind("tcp://0.0.0.0:8081");

    logger->info("Loading last state...");

    // Load state from disk (do not configure yet)
    State state;

    logger->info("Configuring Prometheus...");

    // Expose port 8080 to be scraped
    prometheus::Exposer exposer{"0.0.0.0:8080"};

    // Create registry for prometheus
    auto registry = std::make_shared<prometheus::Registry>();

    // Register counters
    auto& hit_counter = prometheus::BuildCounter()
            .Name("hits")
            .Help("Hits per project")
            .Register(*registry);

    auto& byte_counter = prometheus::BuildCounter()
            .Name("bytes_sent")
            .Help("Bytes sent per project")
            .Register(*registry);

    // Allow scraping of registry by Prometheus
    exposer.RegisterCollectable(registry);

    // Fast forward to last seen event in the log
    if(state.getLastEvent().size() > 0) {
        logger->info("Resuming after last seen event...");
        Event last_event(state.getLastEvent());
        while(true) {
            std::string line;
            getline(std::cin, line);
            mirror::Event event(line);
            if(event > last_event) {
                logger->warn("Last seen event was not found in the NGINX log. "
                        "Continuing from here.");
                break;
            }
            if(event == last_event) { break; }
        }
    }

    // Keep track of when state on disk was last updated
    std::chrono::time_point<std::chrono::system_clock> last_updated = 
            std::chrono::system_clock::now();

    logger->info("Ready.");

    // Start processing events
    while(true) {
        // Parse events from NGINX log on stdin
        std::string line;
        getline(std::cin, line);
        mirror::Event event(line);
        //logger->debug(event.toString());

        // Register event with Prometheus
        std::string project = event.getProject();
        if(project.size() > 0) {
            //logger->debug("Hit on project " + project + ".");
            hit_counter.Add({{"project", project}}).Increment();
            byte_counter.Add({{"project", project}}).Increment((double) event.getBytesSent());
            state.registerLastEvent(line);
            zmq::message_t message{event.toMapString()};
            pubSocket.send(message, zmq::send_flags::none);
        }

        // Periodically save state to disk
        std::chrono::duration<double> since_updated = 
                std::chrono::system_clock::now() - last_updated;
        if(since_updated.count() >= STATE_UPDATE_INTERVAL) {
            state.save();
            last_updated = std::chrono::system_clock::now();
        }
    }
}
