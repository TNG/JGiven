#!/bin/bash
# to run this script locally, set
# SONATYPE_PASSWORD='${sonatypePassword}'
# SONATYPE_USERNAME='${sonatpyeUsername}'
# ANDROID_SDK_ROOT='${androidRoot}'
# JAVA_HOME='${java8Home}'
# and run with the following arguments
#"${version}" "-PsigningKey=$(gpg --export-secret-key -a ${id} | sed -e '/^---.*/d' | tr -d '\n')" "-PsigningPassword=${password}"

SCRIPT_LOCATION=$(dirname -- "$(readlink -f -- "${BASH_SOURCE[0]}")")
# shellcheck source=./release_functions.sh
source "${SCRIPT_LOCATION}/source_files/release_functions.sh"
source "${SCRIPT_LOCATION}/source_files/helper_functions.sh"

set -e

export RELEASE=true
if [ ! -e 'gradle.properties' ]; then
    echo "Script was not executed in root directory"
    exit 1
fi

if [ -n "$(git status --porcelain)" ]; then
    echo "There are local, uncommitted changes, aborting..."
    exit 1
fi

verify_version_present_and_formatted "$1" || exit $?

if [ -z "${ANDROID_SDK_ROOT}" ]; then
    echo "Variable 'ANDROID_SDK_ROOT' not set. Will not continue release because the android package can't be built."
    exit 1
fi

VERSION="$1"
VERSION_PREFIXED="v$1"
declare -a GRADLE_PROPERTIES
find_gradle_property GRADLE_PROPERTIES "$@"

echo Releasing version "${VERSION}"

updateAllVersionInformation $VERSION

if [ -n "$(git status --porcelain)" ]; then
    echo Commiting version change
    git commit -a -m "Update version to $VERSION"
fi

echo Building, Testing...
./gradlew --no-parallel clean test

echo Creating Tag
git tag -a -m "${VERSION_PREFIXED}" "${VERSION_PREFIXED}"

echo "${CI}"
if [[ "${CI}" == "true" ]]
then
  releaseRepositoryAndPushVersion || exit $?
else
  read -p "Do you want to release and push now? [y/N]" -n 1 -r
  if [[ "${REPLY}" =~ ^[Yy]$ ]]
  then
    releaseRepositoryAndPushVersion || exit $?
  fi
fi
