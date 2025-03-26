cmake_minimum_required(VERSION 3.16)
project(kaminari_app)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_AUTOUIC ON)

# Android specific configuration
set(ANDROID_PACKAGE_SOURCE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/android")

# Source files
add_library(${PROJECT_NAME} SHARED 
    main.cpp
    mainwindow.cpp
    FolderWidget.cpp
    NewItemDialog.cpp
    NewFolderDialog.cpp
    SketchPage.cpp
    mainwindow.h
    FolderWidget.h
    NewItemDialog.h
    NewFolderDialog.h
    SketchPage.h
    myresources.qrc
)

# Find Qt packages
find_package(Qt6 COMPONENTS Core Gui Widgets REQUIRED)

# Link the required libraries
target_link_libraries(${PROJECT_NAME} PRIVATE
    Qt6::Core
    Qt6::Gui
    Qt6::Widgets
)

# For Android, link against log library
if(ANDROID)
    find_library(log-lib log)
    target_link_libraries(${PROJECT_NAME} PRIVATE ${log-lib})
endif()