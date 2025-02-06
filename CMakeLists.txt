cmake_minimum_required(VERSION 3.10)
project(kaminari_app)

set(ANDROID_PLATFORM android-30)
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Enable automatic handling of Qt features
set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)

# Correct CMake prefix path for Qt Android installation
set(CMAKE_PREFIX_PATH "/Users/AfamObioha/Qt/6.6.3/android_arm64_v8a/lib/cmake")

# Find required Qt6 modules, excluding Qt6Android since it's not in 6.6.3
find_package(Qt6 REQUIRED COMPONENTS Core Gui Widgets Qml Quick QuickControls2 QuickTemplates2 Network OpenGL)

# Source files
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
    myresources.qrc  # Ensure this is included correctly
)

# Process the .qrc resource file for Android
qt6_add_resources(PROJECT_SOURCES myresources.qrc)

# Define the executable
add_executable(kaminari_app ${PROJECT_SOURCES})

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

# Ensure Qt is deployed properly on Android
set_property(TARGET kaminari_app PROPERTY QT_ANDROID_PACKAGE_SOURCE_DIR "${CMAKE_SOURCE_DIR}/android")

# ✅ Explicitly set JNI library path to match jniLibs
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/app/src/main/jniLibs/${ANDROID_ABI}")