cmake_minimum_required(VERSION 3.22.1)

project("duinocoinminer")

add_library(
        ducohasher
        SHARED
        hasher/ducohasher.cpp
)

add_library(
        cpuinfo
        SHARED
        utils/cpuinfo.cpp
)

find_library(
        log-lib
        log
)

target_link_libraries(
        ducohasher
        cpuinfo
        ${log-lib}
)
