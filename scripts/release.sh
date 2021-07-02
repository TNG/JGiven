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
source "${SCRIPT_LOCATION}/release_functions.sh"
source "${SCRIPT_LOCATION}/helper_functions.sh"

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

if [[ ! $1 =~ ^[0-9]*\.[0-9]*\.[0-9]*(-[A-Z0-9]*)?$ ]]; then
    echo "You have to provide a version as first parameter (without v-prefix, e.g. 0.14.0)"
    exit 1
fi

if [ -z "${ANDROID_SDK_ROOT}" ]; then
    echo "Variable 'ANDROID_SDK_ROOT' not set. Will not continue release because the android package can't be built."
    exit 1
fi

VERSION="$1"
VERSION_PREFIXED="v$1"
declare -a GRADLE_PROPERTIES
find_gradle_property GRADLE_PROPERTIES "$@"

echo Releasing version "${VERSION}"

echo Updating version in gradle.properties...
for file in "gradle.properties" \
"example-projects/junit5/gradle.properties" \
"example-projects/spock/gradle.properties" \
"example-projects/testng/gradle.properties" \
"example-projects/android/gradle.properties" \
"example-projects/selenium/gradle.properties" \
do
  update_version "${VERSION}" "${file}" || exit 1
done

if [ -n "$(git status --porcelain)" ]; then
    echo Commiting version change
    git add gradle.properties
    git commit -m "Update version to $VERSION"
fi

echo Building, Testing, and Uploading Archives...
./gradlew --no-parallel clean test  publishMavenPublicationToMavenLocal publishMavenPublicationToMavenRepository "${GRADLE_PROPERTIES[@]}"

echo Creating Tag
git tag -a -m "${VERSION_PREFIXED}" "${VERSION_PREFIXED}"

echo Closing the repository...
./gradlew closeRepository "${GRADLE_PROPERTIES[@]}"

echo Testing staging version...

echo Testing Maven plugin from staging repository...
mvn -f example-projects/maven/pom.xml clean test -Djgiven.version="${VERSION}"

echo Testing Gradle plugin from staging repository...
./gradlew -b example-projects/junit5/build.gradle clean test -Pstaging -Pversion="${VERSION}" "${GRADLE_PROPERTIES[@]}"

echo STAGING SUCCESSFUL!

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
