cmake_minimum_required(VERSION 3.10) # Updated minimum version for Android compatibility
project(kaminari_app) # Updated project name

set(ANDROID_PLATFORM android-30) # Set to target Android 11 (API level 30)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Enable AUTOMOC, AUTOUIC, and AUTORCC for Qt projects
set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)

# Set CMake prefix path to include Qt for Android installation directory
set(CMAKE_PREFIX_PATH "/Users/AfamObioha/Qt/6.6.3/android/lib/cmake" CACHE STRING "Qt6 Android CMake prefix path")

# Optionally include an environment variable method for flexibility
if(DEFINED ENV{MY_CMAKE_PATH})
    list(APPEND CMAKE_PREFIX_PATH $ENV{MY_CMAKE_PATH})
endif()

# Find the necessary Qt modules for Android
find_package(Qt6 COMPONENTS Widgets Gui Quick REQUIRED)

# Set translation source files if any
set(TS_FILES kaminari_prime_en_US.ts)

# Define the project sources
set(PROJECT_SOURCES
    main.cpp
    mainwindow.cpp
    mainwindow.h
    mainwindow.ui
    FolderWidget.cpp
    FolderWidget.h
    NewItemDialog.cpp
    NewItemDialog.h
    NewFolderDialog.cpp
    NewFolderDialog.h
    SketchPage.cpp
    SketchPage.h
    ${TS_FILES}
)

# Process the .qrc resource file for Android
qt6_add_resources(PROJECT_SOURCES myresources.qrc)

# Create the executable for Android with the project sources
add_executable(kaminari_app ${PROJECT_SOURCES})

# Link the executable to the necessary Qt modules for Android
target_link_libraries(kaminari_app PRIVATE Qt6::Widgets Qt6::Gui Qt6::Quick)