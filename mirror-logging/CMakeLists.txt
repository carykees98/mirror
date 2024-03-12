cmake_minimum_required(VERSION 3.22)
project(mirror-logging VERSION 0.0.0 LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 17)

add_library(mirror-logging src/logger.cpp)
target_link_libraries(mirror-logging zmq)
target_include_directories(mirror-logging PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}/include)
