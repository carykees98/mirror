#include <scheduler/module.hpp>
#include <scheduler/module.hpp>

#include <string>

#include <scheduler/job.hpp>

namespace mirror {
    DispatchJob::DispatchJob(DispatchJob::Tasks task, DispatchModule* target) {
        this->task = task;
        this->target = target;
    }
}