#!/usr/bin/env bash

function find_gradle_property(){
    for command_line_argument in "$@"; do
        gradle_properties=""
        if [[ "${command_line_argument}" =~ ^-P.*=.* ]];then
          gradle_properties="${gradle_properties} ${command_line_argument}"
        fi
        printf "%s" "${gradle_properties}"
    done
}

function releaseRepositoryAndPushVersion()
{
  echo Releasing the repository...
  ./gradlew releaseRepository || return 21

  echo Testing Maven plugin from Maven repository...
  mvn -f example-projects/maven/pom.xml clean test -Djgiven.version="${VERSION}" || return 22

  echo Publishing Gradle Plugin to Gradle Plugin repository...
  ./gradlew -b jgiven-gradle-plugin/build.gradle publishPlugins -Dgradle.publish.key="${GRADLE_PLUGIN_RELEASE_KEY}" -Dgradle.publish.secret="${GRADLE_PLUGIN_RELEASE_SECRET}" || return 23

  echo Testing Gradle Plugin from Gradle Plugin repository...
  ./gradlew -b example-projects/java9/build.gradle clean test -Pversion="${VERSION}" || return 24

  echo Pushing version and tag to GitHub repository...
  git push
  git push "$(git config --get remote.origin.url)" "${VERSION_PREFIXED}" || return 25
}
