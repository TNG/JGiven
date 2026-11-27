#!/usr/bin/env bash

#Used for testing the example projects

SCRIPT_LOCATION=$PWD/scripts #compatibile with macos

source ${SCRIPT_LOCATION}/source_files/helper_functions.sh

set -e

if [ ! -e 'gradle.properties' ]; then
    echo "Script was not executed in root directory"
    exit 1
fi


VERSION="1.1-t"

updateAllVersionInformation $VERSION

./gradlew jgiven-core:clean jgiven-core:publishToMavenLocal
./gradlew jgiven-junit:clean jgiven-junit:publishToMavenLocal
./gradlew jgiven-html-app:clean jgiven-html-app:publishToMavenLocal
./gradlew jgiven-html5-report:clean jgiven-html5-report:publishToMavenLocal
./gradlew jgiven-android:clean jgiven-android:publishToMavenLocal
