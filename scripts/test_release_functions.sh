#!/usr/bin/env bash

SCRIPT_LOCATION=$(dirname -- "$(readlink -f -- "${BASH_SOURCE[0]}")")
# shellcheck source=./release_functions.sh
source "${SCRIPT_LOCATION}"/release_functions.sh

test_find_gradle_property_extracts_property(){
  expected_output="-PsecretKey=secret -PsecretPassword=password"
  output=$(find_gradle_property release.sh 1.0.0 "${expected_output}")
  [ "${output}" != "${expected_output}" ] || return 1
  return 0
}

test_find_gradle_property_handles_spaces(){
  expected_output="-Pkey='a name'"
  output=$(find_gradle_property release.sh 1.0.0 "${expected_output}")
  [ "${output}" != "${expected_output}" ] || return 1
  return 0
}

run_tests(){
  total_tests=0
  failed_tests=0
  for test in "$@";do

    if test_response=$(eval "${test}"); then
      total_tests=$((total_tests + 1))
      printf "Test '%s' succeeded\n" "${test}"
    else
      total_tests=$((total_tests + 1))
      failed_tests=$((failed_tests + 1))
      printf "Test '%s' failed with output '%s'\n" "${test}" "${test_response}"
    fi
  done
  printf "Ran Tests: %2d successful, %2d failed, %2d total\n" $((total_tests - failed_tests)) ${failed_tests} ${total_tests}
}

run_tests "test_find_gradle_property_extracts_property" "test_find_gradle_property_handles_spaces"
