#!/usr/bin/env bash

#Update the version property in a gradle properties file
function update_version(){
  [ $# -eq 2 ] || return 11
  local target_version="$1"
  local target_file="$2"
  local version_property="version"
  local version_matcher="${version_property}=.*"
  if grep -E "${version_matcher}" "${target_file}" ;then
    sed -i -e "s/${version_matcher}/${version_property}=${target_version}/" "${target_file}"
  else
    printf "Could not find version property to replace in file %s\n" "${target_file}"
    return 12
  fi
  return 0
}

function updateScalaVersion() {
  [ $# -eq 2 ] || return 11
  local target_version="$1"
  local target_file="$2"
  local version_property="jgivenVersion"
  local version_matcher="${version_property} = .*"
  if grep -E "${version_matcher}" "${target_file}" ;then
    sed -i -e "s/${version_matcher}/${version_property} = \"${target_version}\"/" "${target_file}"
  else
    printf "Could not find version property to replace in file %s\n" "${target_file}"
    return 12
  fi
  return 0
}

function updateAllVersionInformation() {
  [ $# -eq 1 ] || return 11
  local VERSION="$1"

  echo Updating version in gradle.properties...
  for file in "gradle.properties" \
  "example-projects/junit5/gradle.properties" \
  "example-projects/spock/gradle.properties" \
  "example-projects/testng/gradle.properties" \
  "example-projects/android/gradle.properties" \
  "example-projects/kotlin/gradle.properties" \
  "example-projects/selenium/gradle.properties" \
  "example-projects/spring-boot/gradle.properties"
  do
    update_version "${VERSION}" "${file}" || exit 1
  done

  updateScalaVersion "${VERSION}" "example-projects/scala/build.sbt" || exit 1
}

function runGradleTestOnGivenProject() {
    [ $# -eq 2 ] || return 11
    local givenProject="$1"
    local VERSION="$2"

    ./gradlew -b $givenProject clean test -Pversion=$VERSION
}

function runScalaTest() {
    cd example-projects/scala
    sbt test jgivenReport
    cd ../..
}

function runAndroidTestOnGivenProject() {
    [ $# -eq 2 ] || return 11
    local givenProject="$1"
    local VERSION="$2"

    ./gradlew -b $givenProject clean test connectedAndroidTest -Pversion=$VERSION
}

function runMavenTestOnGivenProject() {
    [ $# -eq 2 ] || return 11
    local givenProject="$1"
    local VERSION="$2"

    mvn -U -f $givenProject clean test -Djgiven.version=$VERSION
}
