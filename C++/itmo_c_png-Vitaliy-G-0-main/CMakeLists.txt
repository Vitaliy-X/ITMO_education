cmake_minimum_required(VERSION 3.24)
project(itmo_c_png_vitaliy_g_0_main)

set(CMAKE_C_STANDARD 99)

find_package(ZLIB REQUIRED)

include_directories(. /usr/local/include /Users/vitalij/isa-l-master/ ${ZLIB_INCLUDE_DIRS})

add_executable(itmo_c_png_vitaliy_g_0_main return_codes.h main.c)
target_link_libraries(itmo_c_png_vitaliy_g_0_main /usr/local/lib/libdeflate.a ${ZLIB_LIBRARIES})

add_definitions(-DLIBDEFLATE)
target_compile_definitions(itmo_c_png_vitaliy_g_0_main PRIVATE -DLIBDEFLATE)