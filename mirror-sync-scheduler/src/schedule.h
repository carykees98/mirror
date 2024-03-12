#pragma once

struct Task{
    std::string name;
    int syncs;
};

struct Job{
    std::vector<std::string> name;
    double target_time;
};

class Schedule{
    private:
    Schedule();
    
    public:
    //delete copy and move constructors
    Schedule(Schedule&) = delete;
    Schedule(Schedule&&) = delete;
    Schedule &operator=(const Schedule &) = delete;
    Schedule &operator=(const Schedule &&) = delete;

    //create the schedule object the first time and return a pointer to that same object every other time its called
    static Schedule* getInstance();

    //use the mirrors.json file to create the schedule 
    void build(json &config);

    //return the next job and the time to sleep till that job using the schedule and the currient time
    std::vector<std::string>* nextJob(int &seconds_to_sleep);

    private: //functions
    //used inside build to run several sanity checks for the schedule
    bool verify(std::vector<Task> tasks);

    //used inside build to parse the mirrors.json file into a vector of tasks
    std::vector<Task> parseTasks(json &config);

    public: //data
    //used to determine if the main loop needs to recalculate nextJob
    std::atomic_bool reloaded;

    private: //data
    int iterator;
    //vector of jobs containing names of jobs to sync and the time to sync them
    std::vector<Job> jobs;
    //connection to log server
    mirror::Logger* logger = mirror::Logger::getInstance();
};