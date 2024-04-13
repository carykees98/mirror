#pragma once

#include <deque>

class Queue{
    public:
    //delete copy and move constructors
    //to prevent creation of multiple queue objects
    Queue(Queue&) = delete;
    Queue(Queue&&) = delete;
    Queue &operator=(const Queue &) = delete;
    Queue &operator=(const Queue &&) = delete;

    //create the queue singleton object the first execution
    //and return the same object with every sequential execution
    //@return pointer to queue singleton object
    static Queue* getInstance();

    //add a vector of jobs to the back of the queue 
    //@param name pointer to a vector of job names
    void push_back_list(std::vector<std::string>* name);

    //sync a project in a new seperate thread from the queue (similar to jobQueueThread)
    //@param name project to sync
    void manual_sync(std::string name);

    //start syncing projects from the queue
    //@param maxThreads number of jobQueueThreads to create to sync jobs in parallel
    void startQueue(std::size_t maxThreads);

    //generate sync commands for every mirror entry in mirrors.json using generateSyncCommands
    //and store the commands in the syncCommands map
    //@param config reference to mirrors.json object
    void createSyncCommandMap(json &config);

    //set the dryrun flag which controls if syncs are echoed or not.
    //@param dr true causes syncs to be "echoed"
    void setDryrun(bool dr);

    private:
    Queue();

    //syncs jobs from the queue in parallel with other jobQueueThreads
    void jobQueueThread();

    //used to sync a given project with the commands from the syncCommandMap
    //@param name project to sync
    void syncProject(std::string name);

    //generate rsync or script commands to sync a specific project
    //@param config reference to mirrors.json object
    //@param name project to generate commands for
    //@return vector of commands needed to sync a project
    std::vector<std::string> generateSyncCommands(json &config, std::string name);

    //create a rsync command inside generateSyncCommands
    //@param config reference to a rsync json object 
    //@param options reference to the options string for this command
    //@return rsync command string
    std::string rsync(json &config, std::string &options);

    private:
    //thread lock to prevent modifying data from multiple sync threads at the same time
    std::mutex tLock;
    //deque of queued jobes to be synced
    std::deque<std::string> queue_;
    //keep track of what jobs we are currently syncing so that we never sync the same task in a different thread at the same time.
    std::vector<std::string> currentJobs;
    //map of projects to the commands needed to sync the project
    std::unordered_map<std::string, std::vector<std::string>> syncCommands;
    //map of password files
    std::unordered_map<std::string, std::string> passwordFiles;
    //used to prevent the queue from being started more than once
    bool queueRunning;
    //used to run program as a "dry run"
    bool dryrun;
    //connection to log server
    mirror::Logger* logger = mirror::Logger::getInstance();
};