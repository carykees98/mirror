#include <chrono>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <string>
#include <thread>
#include <vector>

#include <signal.h>

#include <mirror/logger.hpp>
#include <nlohmann/json.hpp>

#include "queue.hpp"
#include "schedule.hpp"

// read json in from a file
//@param filename std::string json file to read
//@return json object
// TODO: convert file parameter to `const std::filesystem::path&`
auto readJSONFromFile(std::string filename) -> nlohmann::json
{
    std::ifstream  f(filename);
    nlohmann::json config = nlohmann::json::parse(f);
    f.close();
    return config;
}

// thread for handling manual sync through std input
// !: I feel like there has to be a better way to do this
// TODO: Think of alternative methods for manual syncs. `stdin` could be useful
// in addition to API endpoint, but probably not really needed.
auto cin_thread() -> void
{
    // create a pointer to the queue
    Queue* queue = Queue::getInstance();

    while (true)
    {
        std::string x;
        std::cin >> x;
        queue->manual_sync(x);
    }
}

// thread that sends a message to the log server every 29 minutes to keep the
// socket from closing
// ?: Do we actually want to keep alive or let the connection drop and
// reconnect when we need to send a message?
auto keep_alive_thread() -> void
{
    mirror::Logger* logger = mirror::Logger::getInstance();

    while (true)
    {
        // sleep for 29 minutes
        std::this_thread::sleep_for(std::chrono::minutes(29));
        // send keepalive message
        logger->info("keep alive.");
    }
}

// thread that updates the schedule and syncCommandMap whenever there is any
// change made to mirrors.json
auto update_schedule_thread() -> void
{
    // create a pointer to the schedule
    Schedule* schedule = Schedule::getInstance();
    // create a pointer to the queue
    Queue*    queue    = Queue::getInstance();

    const auto mirrorsJSONFile
        = std::filesystem::relative("configs/mirrors.json");
    auto previousModificationTime
        = std::filesystem::last_write_time(mirrorsJSONFile);

    while (true)
    {
        // sleep for 7 seconds because its the smallest number that doesnt
        // divide 60
        // ?: why does the number have to be not divisible by 60?
        std::this_thread::sleep_for(std::chrono::seconds(7));

        const auto currentModificationTime
            = std::filesystem::last_write_time(mirrorsJSONFile);

        if (currentModificationTime != previousModificationTime)
        {
            // TODO: Use logger instead of `std::cout`
            std::cout << "`mirrors.json` last write time has changed. "
                         "Generating new sync schedule\n";

            // retrieve the mirror data from mirrors.json
            nlohmann::json config = readJSONFromFile("configs/mirrors.json");

            // build the schedule based on the mirrors.json config
            schedule->build(config.at("mirrors"));
            // create a new sync command map
            queue->createSyncCommandMap(config.at("mirrors"));
            // set reloaded flag for main thread
            schedule->reloaded = true;

            previousModificationTime = currentModificationTime;
        }
    }
}
