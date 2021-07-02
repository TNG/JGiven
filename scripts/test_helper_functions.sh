#!/usr/bin/env bash

SCRIPT_LOCATION=$(dirname -- "$(readlink -f -- "${BASH_SOURCE[0]}")")
# shellcheck source=./helper_functions.sh
source "${SCRIPT_LOCATION}"/helper_functions.sh
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

test_update_version_fails_if_property_not_in_file() {
  local test_file="properties.tmp"
  trap "rm -f '${test_file}';exit" SIGTERM SIGINT
  echo 'venison=0.x' > "${test_file}"
  update_version '1.0' "${test_file}"
  local output_variable=$?
  rm -f "${test_file}"
  return $( [ ${output_variable} -ge 1 ]; echo $?)
}

run_tests "test_update_version_fails_if_property_not_in_file" \
"test_update_version_replaces_version_property_in_file"
exit $?
