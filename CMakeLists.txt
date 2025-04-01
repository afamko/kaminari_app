cmake_minimum_required(VERSION 3.16)
project(kaminari_app)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_AUTOUIC ON)

# Android specific configuration
set(ANDROID_PACKAGE_SOURCE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/android")

# Set Android specific compile flags
if(ANDROID)
    # Force include of C string functions
    add_definitions(-D_USING_LIBCXX)
    add_definitions(-D__STDC_LIMIT_MACROS)
    add_definitions(-D__STDC_CONSTANT_MACROS)
    
    # Set string.h include path
    include_directories(SYSTEM ${ANDROID_NDK}/sources/cxx-stl/llvm-libc++/include)
    include_directories(SYSTEM ${ANDROID_NDK}/sources/android/support/include)
    
    # Force include of Android includes
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -include string.h -include stdlib.h")
endif()

# Source files
add_library(${PROJECT_NAME} SHARED 
    main.cpp
    mainwindow.cpp
    FolderWidget.cpp
    NewItemDialog.cpp
    NewFolderDialog.cpp
    SketchPage.cpp
    jni_bridge.cpp  # Added JNI bridge file
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
    find_library(android-lib android)
    target_link_libraries(${PROJECT_NAME} PRIVATE 
        ${log-lib}
        ${android-lib}
    )
endif()