#!/bin/bash
# Script to find libc++_shared.so in the NDK

export ANDROID_NDK_HOME="$HOME/Library/Android/sdk/ndk/25.2.9519653"
echo "Searching for libc++_shared.so in NDK directory..."
find "$ANDROID_NDK_HOME" -name "libc++_shared.so" 2>/dev/null

echo "Done searching."
