# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.
project("fridaDetect")
cmake_minimum_required(VERSION 3.4.1)

set(can_use_assembler TRUE)
enable_language(ASM)

#Set flags to detect arm32 bit or arm64 bit for switching between elf structures
if(${ANDROID_ABI} STREQUAL "armeabi-v7a" OR ${ANDROID_ABI} STREQUAL "x86")
    add_definitions("-D_32_BIT")
elseif(${ANDROID_ABI} STREQUAL "arm64-v8a" OR ${ANDROID_ABI} STREQUAL "x86_64")
    add_definitions("-D_64_BIT")
endif()


add_library( # Sets the name of the library.
             detect-frida

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             native-lib.c)

target_include_directories(detect-frida PRIVATE arch/${ANDROID_ABI})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        detect-frida

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
