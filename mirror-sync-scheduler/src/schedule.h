#pragma once

//Task structs are used in the build algorithm to represent a project
//name - name of project
//syncs - number of times the project wants to sync per day
struct Task{
    std::string name;
    int syncs;
};

//Job structs are created by the build algorithm to represent a job to be run at a specific time
//name - vector of names of all the projects that will be queued
//target_time - double between 0 and 1 representing the fraction of a day till its ran
struct Job{
    std::vector<std::string> name;
    double target_time;
};

class Schedule{
    private:
    Schedule();
    
    public:
    //delete copy and move constructors
    //to prevent creation of multiple schedule objects
    Schedule(Schedule&) = delete;
    Schedule(Schedule&&) = delete;
    Schedule &operator=(const Schedule &) = delete;
    Schedule &operator=(const Schedule &&) = delete;

    //create the schedule singleton object the first execution
    //and return the same object with every sequential execution
    //@return pointer to schedule singleton object
    static Schedule* getInstance();

    //use the mirrors.json file to create the schedule by populating the jobs vector
    //@param config reference to mirrors.json object
    void build(json &config);

    //calculate the next job to be updated using the schedule and the time to sleep till then
    //@param seconds_to_sleep updated to the time to sleep till the next job (pass by refrence)
    //@return pointer to a vector of strings with the names of the projects to be updated next
    std::vector<std::string>* nextJob(int &seconds_to_sleep);

    private:
    //used inside build to run several sanity checks for the schedule
    //@param tasks vector of Task structs
    //@return bool representing pass/fail of sanity checks
    bool verify(std::vector<Task> tasks);

    //used inside build to parse the mirrors.json file into a vector of Task structs
    //@param config reference to mirrors.json object
    //@return vector of Task structs for each project
    std::vector<Task> parseTasks(json &config);

    public:
    //used inside the main function to break out of the sleep loop to recalculate the next job
    std::atomic_bool reloaded;

    private:
    //used to hold the most recently ran job to speed up nextJob()
    int iterator;
    //vector of Jobs containing names of Tasks to sync and the time to sync them
    std::vector<Job> jobs;
    //connection to log server
    mirror::Logger* logger = mirror::Logger::getInstance();
};