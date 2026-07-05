cmake_minimum_required(VERSION 4.2)
project(DP)

set(CMAKE_CXX_STANDARD 14)

include_directories(.)

add_executable(DP
        fibonacci.cpp
        friendsPairing.cpp
        tiling.cpp
        coinExchange_unlimited-coins.cpp
        coinExchange_limited-coins.cpp
        coinExchange_k-coins.cpp
        coinExchange_min-coins.cpp
        knapsack.cpp)
