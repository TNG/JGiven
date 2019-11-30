#!/bin/bash

set -xe

mvn -f example-projects/java11/pom.xml test
./gradlew -b example-projects/junit5/build.gradle test
