#!/usr/bin/env bash

echo Version $1

if [ -z "$1" ]; then
    echo "No version provided"
    exit 1
fi

echo "Creating release post for version $1"

cp _drafts/released.md _publish/$1

jekyll serve --watch
