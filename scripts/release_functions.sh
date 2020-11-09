#!/usr/bin/env bash

function find_gradle_property(){
    [[ "$(declare -p $1 2>/dev/null)" =~ "declare -a" ]] || return 11
    local -n output_target=$1
    for command_line_argument in "$@"; do
        if [[ "${command_line_argument}" =~ ^-P.*=.* ]];then
          output_target+=("${command_line_argument}")
        fi
    done
}

function releaseRepositoryAndPushVersion()
{
  echo Releasing the repository...
  ./gradlew releaseRepository || return 21

  echo Testing Maven plugin from Maven repository...
  mvn -f example-projects/maven/pom.xml clean test -Djgiven.version="${VERSION}" || return 22

  echo Publishing Gradle Plugin to Gradle Plugin repository...
  ./gradlew -b jgiven-gradle-plugin/build.gradle publishPlugins -Pgradle.publish.key="${GRADLE_PLUGIN_RELEASE_KEY}" -Pgradle.publish.secret="${GRADLE_PLUGIN_RELEASE_SECRET}" || return 23

  echo Testing Gradle Plugin from Gradle Plugin repository...
  ./gradlew -b example-projects/java9/build.gradle clean test -Pversion="${VERSION}" || return 24

  echo Pushing version and tag to GitHub repository...
  git push
  git push "$(git config --get remote.origin.url)" "${VERSION_PREFIXED}" || return 25
}
