#!/bin/bash

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

if [ -z $ANDROID_SDK_ROOT ]; then
    echo "Variable 'ANDROID_SDK_ROOT' not set. Will not continue release because the android package can't be built."
    exit 1
fi

VERSION=$1
VERSION_PREFIXED="v$1"

echo Releasing version $VERSION

echo Updating version in gradle.properties...
sed -i -e s/version=.*/version=$VERSION/ gradle.properties

if [ -n "$(git status --porcelain)" ]; then
    echo Commiting version change
    git add gradle.properties
    git commit -m "Update version to $VERSION"
fi

echo Building, Testing, and Uploading Archives...
./gradlew --no-parallel clean test publishMavenPublicationToMavenRepository

echo Creating Tag
git tag -a -m $VERSION_PREFIXED $VERSION_PREFIXED

echo Closing the repository...
./gradlew closeRepository

echo Testing staging version...

echo Testing Maven plugin from staging repository...
mvn -f example-projects/maven/pom.xml clean test -Djgiven.version=$VERSION

echo Testing Gradle plugin from staging repository...
./gradlew -b example-projects/junit5/build.gradle clean test -Pstaging -Pversion=$VERSION

echo STAGING SUCCESSFUL!

releaseRepositoryAndPushVersion()
{
  echo Releasing the repository...
  ./gradlew releaseRepository

  echo Testing Maven plugin from Maven repository...
  mvn -f example-projects/maven/pom.xml clean test -Djgiven.version=$VERSION

  echo Publishing Gradle Plugin to Gradle Plugin repository...
  ./gradlew -b jgiven-gradle-plugin/build.gradle publishPlugins -Dgradle.publish.key=$GRADLE_PLUGIN_RELEASE_KEY -Dgradle.publish.secret=$GRADLE_PLUGIN_RELEASE_SECRET

  echo Testing Gradle Plugin from Gradle Plugin repository...
  ./gradlew -b example-projects/java9/build.gradle clean test -Pversion=$VERSION

  echo Pushing version and tag to GitHub repository...
  git push
  git push $(git config --get remote.origin.url) $VERSION_PREFIXED
}

echo $CI
if [[ $CI == "true" ]]
then
  releaseRepositoryAndPushVersion
else
  read -p "Do you want to release and push now? [y/N]" -n 1 -r
  if [[ $REPLY =~ ^[Yy]$ ]]
  then
    releaseRepositoryAndPushVersion
  fi
fi
