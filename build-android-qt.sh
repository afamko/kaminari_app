#!/bin/bash
set -e  # Exit on errors for most of the script
set +e  # Temporarily allow errors for the androiddeployqt command

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

# Configure with CMake
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

# Copy Qt libraries based on what we saw in the file listings
echo "Copying Qt libraries..."
QT_LIBS=(
  "libQt6Core_arm64-v8a.so"
  "libQt6Gui_arm64-v8a.so"
  "libQt6Widgets_arm64-v8a.so"
  "libQt6Network_arm64-v8a.so"
  "libQt6OpenGL_arm64-v8a.so"
  "libQt6Qml_arm64-v8a.so"
  "libQt6QmlModels_arm64-v8a.so"
  "libQt6QmlWorkerScript_arm64-v8a.so"
  "libQt6Quick_arm64-v8a.so"
  "libQt6QuickTemplates2_arm64-v8a.so"
  "libQt6QuickControls2_arm64-v8a.so"
  "libQt6QuickControls2Impl_arm64-v8a.so"
  "libQt6QuickControls2BasicStyleImpl_arm64-v8a.so"
  "libQt6QuickControls2Basic_arm64-v8a.so"
  "libQt6QuickControls2MaterialStyleImpl_arm64-v8a.so"
  "libQt6QuickControls2Material_arm64-v8a.so"
  "libQt6QuickControls2FusionStyleImpl_arm64-v8a.so"
  "libQt6QuickControls2Fusion_arm64-v8a.so"
  "libQt6QuickControls2ImagineStyleImpl_arm64-v8a.so"
  "libQt6QuickControls2Imagine_arm64-v8a.so"
  "libQt6QuickControls2UniversalStyleImpl_arm64-v8a.so"
  "libQt6QuickControls2Universal_arm64-v8a.so"
  "libQt6QuickParticles_arm64-v8a.so"
  "libQt6ShaderTools_arm64-v8a.so"
)

for lib in "${QT_LIBS[@]}"; do
  if [ -f "$QT_PATH/lib/$lib" ]; then
    cp "$QT_PATH/lib/$lib" android-build/src/main/jniLibs/arm64-v8a/
    echo "  Copied $lib"
  else
    echo "  Skipped $lib (not found)"
  fi
done

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
