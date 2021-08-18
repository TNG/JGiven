#!/usr/bin/env bash

#Used for testing the example projects

SCRIPT_LOCATION=$PWD/scripts #compatibile with macos

source ${SCRIPT_LOCATION}/helper_functions.sh

set -e

if [ ! -e 'gradle.properties' ]; then
    echo "Script was not executed in root directory"
    exit 1
fi


VERSION="1.1-t"

updateAllVersionInformation $VERSION

./gradlew -b jgiven-core/build.gradle clean publishToMavenLocal
./gradlew -b jgiven-junit/build.gradle clean publishToMavenLocal
./gradlew -b jgiven-html-app/build.gradle clean publishToMavenLocal
./gradlew -b jgiven-html5-report/build.gradle clean publishToMavenLocal
./gradlew -b jgiven-android/build.gradle clean publishToMavenLocal
