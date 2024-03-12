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

#define STATE_UPDATE_INTERVAL 1.0

using namespace mirror;

int main() {
    // Connect to log server
    Logger* logger = Logger::getInstance();
    logger->configure(4001, "Metrics Engine", "mirrorlog");

    // Restore application state from disk
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

    // Restore counters' state
    for(auto const &i : state.getHits()) {
        hit_counter.Add({{"project", i.first}}).Increment(i.second);
    }

    for(auto const &i : state.getBytesSent()) {
        byte_counter.Add({{"project", i.first}}).Increment(i.second);
    }

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
                logger->warn("Last seen event was not found in the NGINX log."
                        "Continuing from here.");
                break;
            }
            if(event == last_event) { break; }
        }
    }

    // Keep track of when state on disk was last updated
    std::chrono::time_point<std::chrono::system_clock> last_updated = 
            std::chrono::system_clock::now();

    logger->info("Ready to process events.");

    // Start processing events
    while(true) {
        // Parse events from NGINX log on stdin
        std::string line;
        getline(std::cin, line);
        mirror::Event event(line);
        logger->debug(event.toString());

        // Register event with Prometheus
        std::string project = event.getProject();
        if(project.size() > 0) {
            logger->debug("Hit on project " + project + ".");
            state.registerHit(project);
            hit_counter.Add({{"project", project}}).Increment();
            state.registerBytesSent(project, event.getBytesSent());
            byte_counter.Add({{"project", project}}).Increment(event.getBytesSent());
            state.registerLastEvent(line);
        } else {
            logger->debug("Hit - Invalid path / request.");
            hit_counter.Add({{"project", "invalid"}}).Increment();
            byte_counter.Add({{"project", project}}).Increment(event.getBytesSent());
        }

        // Periodically save state to disk
        std::chrono::duration<double> since_updated = 
                std::chrono::system_clock::now() - last_updated;
        if(since_updated.count() >= STATE_UPDATE_INTERVAL) {
            std::cout << since_updated.count() << std::endl;
            state.save();
            last_updated = std::chrono::system_clock::now();
        }
    }
}
