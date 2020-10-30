#!/bin/sh
set -ex
./gradlew asciidoctor
git checkout gh-pages
git pull --rebase
#git rm -fr userguide/*
mkdir -p userguide
cp -r build/docs/asciidoc/* userguide
git add userguide
git commit -m 'added userguide to gh-pages'
git push
git checkout -f master
