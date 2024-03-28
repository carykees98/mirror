cmake_minimum_required(VERSION 3.22)
project(mirror-metrics VERSION 0.0.0 LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 17)

include(FetchContent)

FetchContent_Declare(prometheus-cpp
    GIT_REPOSITORY https://github.com/jupp0r/prometheus-cpp
    GIT_TAG v1.2.4
)
FetchContent_MakeAvailable(prometheus-cpp)

FetchContent_Declare(json
    GIT_REPOSITORY https://github.com/nlohmann/json
    GIT_TAG v3.11.2
)
FetchContent_MakeAvailable(json)

# External libraries from source
add_subdirectory(lib/mirror-logging)

# Include header files
include_directories(${CMAKE_SOURCE_DIR}/src)

# Internal libraries from source
add_library(event
    src/event/event.cpp
    src/event/timestamp.cpp
)

add_library(state
    src/state/state.cpp
)

# Libraries to be linked internally
target_link_libraries(event
    mirror-logging
    nlohmann_json::nlohmann_json
)

target_link_libraries(state
    mirror-logging
    event
    nlohmann_json::nlohmann_json
)

# Libraries to be linked to executable
link_libraries(
    mirror-logging
    event
    prometheus-cpp::pull
    state
    zmq
)
add_executable(mirror-metrics src/main.cpp)
