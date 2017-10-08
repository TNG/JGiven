#!/bin/bash

VERSION=$1

echo "Testing Maven plugin..."
mvn -U -f example-projects/maven/pom.xml clean test -Djgiven.version=$VERSION

echo "Testing Gradle plugin..."
./gradlew -b example-projects/junit5/build.gradle clean test -Pversion=$VERSION


