cmake_minimum_required(VERSION 3.20)
project(viterby CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_executable(viterby main.cpp)

execute_process(
        COMMAND xcrun --show-sdk-path
        OUTPUT_VARIABLE MACOS_SDK
        OUTPUT_STRIP_TRAILING_WHITESPACE
)

target_compile_options(viterby PRIVATE
        -isysroot "${MACOS_SDK}"
)

target_include_directories(viterby SYSTEM PRIVATE
        "${MACOS_SDK}/usr/include/c++/v1"
)
