#!/bin/sh
set -e
./gradlew clean build
git checkout gh-pages
git pull --rebase
TARGET_DIR=snapshot/jgiven-report
git rm -rf ${TARGET_DIR}/*
mkdir -p ${TARGET_DIR}/html5
cp -r jgiven-tests/build/reports/jgiven/html5/ ${TARGET_DIR}
git add ${TARGET_DIR}
git commit -m 'added generated snapshot jgiven report to gh-pages'
git push
git checkout -f master
