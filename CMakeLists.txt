cmake_minimum_required(VERSION 3.10)
project(kaminari_app)

set(ANDROID_PLATFORM android-30)
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Force maximum visibility for JNI functions
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fexceptions -fvisibility=default")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fexceptions -fvisibility=default")
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

# Enable automatic handling of Qt features
set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)

# Correct CMake prefix path for Qt Android installation
set(CMAKE_PREFIX_PATH "/Users/AfamObioha/Qt/6.6.3/android_arm64_v8a/lib/cmake")

# Find required Qt6 modules
find_package(Qt6 REQUIRED COMPONENTS Core Gui Widgets Qml Quick QuickControls2 QuickTemplates2 Network OpenGL)

# Source files
set(PROJECT_SOURCES
    main.cpp
    jni_bridge.cpp  # Add explicit JNI bridge file
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
    myresources.qrc  # Ensure this is included correctly
)

# Process the .qrc resource file for Android
qt6_add_resources(PROJECT_SOURCES myresources.qrc)

# Define shared library instead of executable
add_library(kaminari_app SHARED ${PROJECT_SOURCES})

# Explicitly set JNI export flags with maximum compatibility
target_compile_options(kaminari_app PRIVATE -fvisibility=default)
target_compile_definitions(kaminari_app PRIVATE 
    "JNIEXPORT=__attribute__((visibility(\"default\")))"
    "JNI_EXPORT=__attribute__((visibility(\"default\")))"
)

# Link against necessary Qt libraries
target_link_libraries(kaminari_app PRIVATE
    Qt6::Core
    Qt6::Gui
    Qt6::Widgets
    Qt6::Qml
    Qt6::Quick
    Qt6::QuickControls2
    Qt6::QuickTemplates2
    Qt6::Network
    Qt6::OpenGL
)

# For Android, also link against log library for Android logging
if(ANDROID)
    find_library(log-lib log)
    target_link_libraries(kaminari_app PRIVATE ${log-lib})
endif()

# Deploy Qt to Android
set_property(TARGET kaminari_app PROPERTY QT_ANDROID_PACKAGE_SOURCE_DIR "${CMAKE_SOURCE_DIR}/android")

# Install the .so file correctly
install(TARGETS kaminari_app LIBRARY DESTINATION lib)

# Set JNI output directory
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/app/src/main/jniLibs/${ANDROID_ABI}")