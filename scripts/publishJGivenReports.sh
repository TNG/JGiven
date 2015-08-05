#!/bin/sh
set -e
./gradlew clean build
git checkout gh-pages
git pull --rebase
git rm -r jgiven-report/*
mkdir -p jgiven-report/html5
cp -r jgiven-tests/build/reports/jgiven/html5/ jgiven-report/
git add jgiven-report/
git commit -m 'added generated jgiven report to gh-pages'
git push
git checkout -f master
