#!/bin/sh
git checkout gh-pages
git rm -r jgiven-report/*
cp -r jgiven-tests/target/jgiven-reports/html/ jgiven-report
git add jgiven-report/
git commit -m 'added generated jgiven report to gh-pages'
git push
git checkout -f master
