#!/bin/sh
set -ex
./gradlew javadoc
git checkout gh-pages
git pull --rebase
git rm -r javadoc/*
mkdir -p javadoc
cp -r jgiven-core/build/docs/javadoc/* javadoc
git add javadoc
git commit -m 'added javadoc to gh-pages'
git push
git checkout -f master
