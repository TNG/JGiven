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

VERSION=$1
VERSION_PREFIXED="v$1"

echo Releasing version $VERSION

echo Updating version in gradle.properties...
sed -i -e s/version=.*/version=$VERSION/ gradle.properties

echo Commiting version change
git add gradle.properties

git commit -m "Update version to $VERSION"

echo Building, Testing, and Uploading Archives...
./gradlew clean test install uploadArchives

echo Creating Tag
git tag -a -m $VERSION_PREFIXED $VERSION_PREFIXED

#echo "Promoting the release"
#./gradlew closeAndPromoteRepository

echo DONE
