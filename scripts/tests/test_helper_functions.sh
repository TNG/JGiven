#!/usr/bin/env bash

SCRIPT_LOCATION=$(dirname -- "$(readlink -f -- "${BASH_SOURCE[0]}")")
# shellcheck source=./helper_functions.sh
source "${SCRIPT_LOCATION}"/../source_files/helper_functions.sh
source "${SCRIPT_LOCATION}"/test_runner.sh

test_update_version_replaces_version_property_in_file() {
  local test_file="properties.tmp"
  trap "rm -f '${test_file}';exit" SIGTERM SIGINT
  echo 'version=0.x' > "${test_file}"
  update_version '1.0' "${test_file}"
  grep 'version=1.0' "${test_file}"
  local output_variable=$?
  rm -f "${test_file}"
  return ${output_variable}
}

test_update_version_replaces_version_property_in_scala_files(){
  local test_file="properties.tmp"
  trap "rm -f '${test_file}';exit" SIGTERM SIGINT
  echo 'jgivenVersion = 0.x' > "${test_file}"
  update_scala_version '1.0' "${test_file}"
  grep 'jgivenVersion = "1.0"' "${test_file}"
  local output_variable=$?
  rm -f "${test_file}"
  return ${output_variable}
}

test_update_version_replaces_version_in_pom(){
  local test_file="properties.tmp"
  trap "rm -f '${test_file}';exit" SIGTERM SIGINT
  echo "<jgiven.version>0.x</jgiven.version>" > "${test_file}"
  update_maven_version '1.0' "${test_file}"
  grep '<jgiven.version>1.0</jgiven.version>' "${test_file}"
  local output_variable=$?
  rm -f "${test_file}"
  return ${output_variable}
}

test_update_version_fails_if_property_not_in_file() {
  local test_file="properties.tmp"
  trap "rm -f '${test_file}';exit" SIGTERM SIGINT EXIT
  echo 'venison=0.x' > "${test_file}"
  update_version '1.0' "${test_file}"
  local output_variable=$?
  return $( [ ${output_variable} -ge 1 ]; echo $?)
}

test_hardcode_gradle_plugin_version_for_example_tests(){
  local properties_file="properties.tmp"
  local build_file="build.tmp"
  trap "rm '${build_file}' '${properties_file}';exit" SIGINT SIGTERM EXIT
  echo "version=1.2.1" > "${properties_file}"
  echo 'id "com.tngtech.jgiven.gradle-plugin" version "${version}"' > "${build_file}"
  hardcodeCurrentPublicVersionForGradlePlugin "${build_file}" "${properties_file}"
  if grep -q 'version "1.2.1"' "${build_file}";then
    return 0
  else
    echo 'Expected to find "version 1.2.1" in build file, but did not.'
    return 1
  fi
}

run_tests "test_update_version_fails_if_property_not_in_file" \
"test_update_version_replaces_version_property_in_file" \
"test_update_version_replaces_version_property_in_scala_files" \
"test_update_version_replaces_version_in_pom" \
"test_hardcode_gradle_plugin_version_for_example_tests"
exit $?
