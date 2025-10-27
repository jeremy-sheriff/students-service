#!/bin/bash

# Define default version if VERSION file is missing or empty
default_version="3.0.7"

# Check if VERSION file exists and is not empty
if [ ! -f VERSION ] || [ ! -s VERSION ]; then
  echo "VERSION file is missing or empty. Initializing with default version $default_version."
  echo "$default_version" > VERSION
fi

# Read the current version from the VERSION file
version=$(cat VERSION)

# Debugging: Output the current version
echo "Current version: $version"

# Split the version into major, minor, and patch parts
IFS='.' read -r major minor patch <<< "$version"

# Debugging: Output the split version parts
echo "Major: $major, Minor: $minor, Patch: $patch"

# Check if splitting was successful and all parts are valid
if [ -z "$major" ] || [ -z "$minor" ] || [ -z "$patch" ]; then
  echo "Error: Version format is incorrect. Resetting to default version $default_version."
  major=3
  minor=0
  patch=7
fi

# Increment the patch version
patch=$((patch + 1))

# If patch reaches 10, reset patch to 0 and increment minor version
if [ "$patch" -ge 10 ]; then
  patch=0
  minor=$((minor + 1))
fi

# If minor reaches 10, reset minor to 0 and increment major version
if [ "$minor" -ge 10 ]; then
  minor=0
  major=$((major + 1))
fi

# Combine them back to form the new version
new_version="$major.$minor.$patch"

# Update the VERSION file with the new version
echo "$new_version" > VERSION

# Output the new version for visibility
echo "New version: $new_version"

# Run your build commands
./gradlew clean &&
./gradlew build -x test &&
docker build --platform linux/amd64 -t muhohoweb/school-app-students:"$new_version" . &&
docker push muhohoweb/school-app-students:"$new_version" &&

# Git add, commit, and push
git add VERSION &&
git commit -m " Add column level encryption/decryption $new_version" &&
git push origin main &&

# Create and push Git tag
git tag "$new_version" &&
git push origin "$new_version"

# Output the final version
echo "Version updated to: $new_version"
