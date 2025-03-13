#!/bin/bash
# Script to rename Qt libraries to the format expected by Android

SOURCE_DIR="app/src/main/jniLibs/arm64-v8a"
TARGET_DIR="app/src/main/jniLibs_renamed/arm64-v8a"

# Create target directory
mkdir -p "$TARGET_DIR"

# Loop through all Qt libraries and copy with renamed format
for file in "$SOURCE_DIR"/libQt6*_arm64-v8a.so; do
  if [ -f "$file" ]; then
    # Extract basename
    basename=$(basename "$file")
    # Remove the _arm64-v8a suffix
    newname=$(echo "$basename" | sed 's/_arm64-v8a//')
    # Copy with new name
    cp "$file" "$TARGET_DIR/$newname"
    echo "Copied $basename to $newname"
  fi
done

# Copy the C++ shared library if it exists
if [ -f "$SOURCE_DIR/libc++_shared.so" ]; then
  cp "$SOURCE_DIR/libc++_shared.so" "$TARGET_DIR/"
  echo "Copied libc++_shared.so"
fi

echo "Library renaming complete"
