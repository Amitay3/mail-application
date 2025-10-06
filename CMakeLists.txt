# CMakeLists.txt
cmake_minimum_required(VERSION 3.14)
project(my_project)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

option(ENABLE_TESTS "Build tests" OFF)

if(ENABLE_TESTS)
    include(FetchContent)
    FetchContent_Declare(
      googletest
      URL https://github.com/google/googletest/archive/03597a01ee50ed33e9dfd640b249b4be3799d395.zip
    )
    set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
    FetchContent_MakeAvailable(googletest)
endif()

# Match the layout inside the container
include_directories(src/bloom-server)

add_library(BloomFilter    src/bloom-server/BloomFilter.cpp)
add_library(UrlFileLoader  src/bloom-server/UrlFileLoader.cpp)
add_library(BitArrayLoader src/bloom-server/BitArrayLoader.cpp)
add_library(UrlFileSaver   src/bloom-server/UrlFileSaver.cpp)
add_library(BitArraySaver  src/bloom-server/BitArraySaver.cpp)
add_library(Server         src/bloom-server/Server.cpp)
add_library(LogicHandler   src/bloom-server/LogicHandler.cpp)
add_library(SocketManager  src/bloom-server/SocketManager.cpp)
add_library(ThreadPool     src/bloom-server/ThreadPool.cpp)

add_executable(bloomfilter_app src/bloom-server/main.cpp)

target_link_libraries(bloomfilter_app
    BloomFilter
    UrlFileLoader
    BitArrayLoader
    UrlFileSaver
    BitArraySaver
    Server
    LogicHandler
    SocketManager
    ThreadPool
)

if(ENABLE_TESTS)
    add_executable(runTests
        tests/test_bloom.cpp
        tests/test_FileLoader.cpp
        tests/test_UrlFileSaver.cpp
        tests/test_BitArraySaver.cpp
        tests/test_Server.cpp
        tests/test_LogicHandler.cpp
        tests/test_SocketManager.cpp
    )

    target_link_libraries(runTests
        gtest_main
        BloomFilter
        UrlFileLoader
        BitArrayLoader
        UrlFileSaver
        BitArraySaver
        Server
        LogicHandler
        SocketManager
        ThreadPool
    )

    include(GoogleTest)
    gtest_discover_tests(runTests)
endif()
