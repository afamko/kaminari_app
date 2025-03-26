#!/bin/bash
set -e

# Go to project directory
cd "$(dirname "$0")"

# Set environment variables
export ANDROID_NDK_HOME="$HOME/Library/Android/sdk/ndk/25.2.9519653"
export QT_PATH="/Users/AfamObioha/Qt/6.6.3/android_arm64_v8a"
STL_LIB_PATH="$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/lib/aarch64-linux-android/libc++_shared.so"

echo "Building Kaminari App with QtActivity approach..."
echo "Using NDK: $ANDROID_NDK_HOME"
echo "Using Qt: $QT_PATH"

# Clean up previous build
rm -rf build-android
mkdir -p build-android
cd build-android

# Build with CMake
echo "Building with CMake..."
cmake .. \
  -DCMAKE_TOOLCHAIN_FILE="$ANDROID_NDK_HOME/build/cmake/android.toolchain.cmake" \
  -DANDROID_ABI=arm64-v8a \
  -DANDROID_PLATFORM=android-29 \
  -DQt6_DIR="$QT_PATH/lib/cmake/Qt6" \
  -DQt6Core_DIR="$QT_PATH/lib/cmake/Qt6Core" \
  -DQt6Gui_DIR="$QT_PATH/lib/cmake/Qt6Gui" \
  -DQt6Widgets_DIR="$QT_PATH/lib/cmake/Qt6Widgets"

cmake --build .

# Create the necessary directories for libraries
mkdir -p android-build/src/main/jniLibs/arm64-v8a

# Copy the application library
cp libkaminari_app.so android-build/src/main/jniLibs/arm64-v8a/
echo "Copied app library to android-build/src/main/jniLibs/arm64-v8a/"

# Copy the STL library
cp "$STL_LIB_PATH" android-build/src/main/jniLibs/arm64-v8a/
echo "Copied STL library to android-build/src/main/jniLibs/arm64-v8a/"

# Copy Java files to the right location
mkdir -p android-build/src/main/java/org/qtproject/qt6/android/bindings
cp ../android/src/org/qtproject/qt6/android/bindings/QtActivity.java android-build/src/main/java/org/qtproject/qt6/android/bindings/
cp ../android/src/org/qtproject/qt6/android/bindings/QtApplication.java android-build/src/main/java/org/qtproject/qt6/android/bindings/
mkdir -p android-build/src/main/java/org/qtproject/qt6/android
cp ../android/src/org/qtproject/qt6/android/QtNative.java android-build/src/main/java/org/qtproject/qt6/android/

# Copy AndroidManifest.xml to the right location
mkdir -p android-build/src/main
cp ../android/AndroidManifest.xml android-build/src/main/AndroidManifest.xml

# Copy resources
mkdir -p android-build/src/main/res/values
cp ../android/res/values/libs.xml android-build/src/main/res/values/

# Create icon resources (missing in the error)
mkdir -p android-build/src/main/res/drawable
mkdir -p android-build/src/main/res/drawable-hdpi
mkdir -p android-build/src/main/res/drawable-mdpi
mkdir -p android-build/src/main/res/drawable-xhdpi
mkdir -p android-build/src/main/res/drawable-xxhdpi

# Create a simple icon XML (vector drawable)
cat > android-build/src/main/res/drawable/icon.xml << EOF
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#3DDC84"
        android:pathData="M0,0h108v108h-108z" />
    <path
        android:fillColor="#00000000"
        android:pathData="M9,0L9,108"
        android:strokeWidth="0.8"
        android:strokeColor="#33FFFFFF" />
</vector>
EOF

# Copy the icon XML to all required drawable folders
cp android-build/src/main/res/drawable/icon.xml android-build/src/main/res/drawable-hdpi/
cp android-build/src/main/res/drawable/icon.xml android-build/src/main/res/drawable-mdpi/
cp android-build/src/main/res/drawable/icon.xml android-build/src/main/res/drawable-xhdpi/
cp android-build/src/main/res/drawable/icon.xml android-build/src/main/res/drawable-xxhdpi/

# Create build.gradle with proper plugins and dependencies
cd android-build
cat > build.gradle << EOF
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:7.4.2'
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

apply plugin: 'com.android.application'

android {
    compileSdkVersion 33
    namespace 'com.AfamObioha.kaminari_app'
    
    defaultConfig {
        applicationId "com.AfamObioha.kaminari_app"
        minSdkVersion 21
        targetSdkVersion 33
        versionCode 1
        versionName "1.0"
    }
    
    sourceSets {
        main {
            manifest.srcFile 'src/main/AndroidManifest.xml'
            java.srcDirs = ['src/main/java']
            res.srcDirs = ['src/main/res']
            assets.srcDirs = ['src/main/assets']
            jniLibs.srcDirs = ['src/main/jniLibs']
        }
    }
    
    packagingOptions {
        exclude 'META-INF/LICENSE.txt'
        exclude 'META-INF/NOTICE.txt'
    }
}
EOF

# Create settings.gradle
cat > settings.gradle << EOF
rootProject.name = 'kaminari_app'
EOF

# Create gradle.properties
cat > gradle.properties << EOF
org.gradle.jvmargs=-Xmx2048m
android.useAndroidX=true
EOF

# Install the gradle wrapper directly (no task needed)
echo "Creating Gradle wrapper directly..."
gradle --no-daemon wrapper

# Build the APK
echo "Building APK with Gradle..."
./gradlew --no-daemon assembleDebug

# Check for APK
if find . -name "*.apk" 2>/dev/null | grep -q ".apk"; then
  echo "✅ Success! APK files found:"
  find . -name "*.apk" 2>/dev/null
  exit 0
else
  echo "❌ Gradle build failed."
  echo "Check the logs above for errors."
  exit 1
fi
