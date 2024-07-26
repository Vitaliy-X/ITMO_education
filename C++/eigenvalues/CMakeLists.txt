cmake_minimum_required(VERSION 3.24)
project(Eigenvalues C)

set(CMAKE_C_STANDARD 11)

include_directories(.)

add_executable(Eigenvalues
        eigenvalues.c
        return_codes.h)
