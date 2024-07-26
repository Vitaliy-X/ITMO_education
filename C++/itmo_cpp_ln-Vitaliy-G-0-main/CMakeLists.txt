cmake_minimum_required(VERSION 3.24)
project(itmo_cpp_ln_Vitaliy_G_0_main)

set(CMAKE_CXX_STANDARD 17)

include_directories(.)

add_executable(itmo_cpp_ln_Vitaliy_G_0_main
        main.cpp
        return_codes.h
        LN.h LN.cpp
        vectorLN.h vectorLN.cpp
        custom_exceptions.h)
