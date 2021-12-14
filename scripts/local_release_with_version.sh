#!/usr/bin/env bash

#Used for testing the example projects

SCRIPT_LOCATION=$(dirname -- "$(readlink -f -- "${BASH_SOURCE[0]}")")

source ${SCRIPT_LOCATION}/source_files/helper_functions.sh

set -e

if [ ! -e 'gradle.properties' ]; then
    echo "Script was not executed in root directory"
    exit 1
fi


VERSION="1.1-t"

updateAllVersionInformation $VERSION

./gradlew clean publishToMavenLocal
