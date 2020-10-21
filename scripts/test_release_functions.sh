#!/usr/bin/env bash

SCRIPT_LOCATION=$(dirname -- "$(readlink -f -- "${BASH_SOURCE[0]}")")
# shellcheck source=./release_functions.sh
source "${SCRIPT_LOCATION}"/release_functions.sh

test_find_gradle_property_check_fails_if_variable_is_undeclared(){
  unset undeclared_variable # Just to be safe that our test variable is not declared
  find_gradle_property undeclared_variable -Pkey=value || return 0
  printf "Expected function to fail on an undeclared variable"
  return 1
}

test_find_gradle_property_check_fails_if_variable_is_not_an_array(){
  local not_an_array_variable
  find_gradle_property not_an_array_variable -Pkey=value || return 0
  printf "Expected function to fail if variable is not an array"
  return 1
}

test_find_gradle_property_writes_to_array_variable(){
  local -a an_empty_array
  find_gradle_property an_empty_array -Pkey=value
  [ "${#an_empty_array[@]}" -eq 1 ] && return 0
  printf "Expected function to write to variable"
  return 1
}

test_find_gradle_property_extracts_property(){
  expected_output="-PsecretKey=secret -PsecretPassword=password"
  local -a output_array
  find_gradle_property release.sh 1.0.0 "${expected_output}"
  printf "Expected \"%s\", got \"%s\"" "${expected_output}" "${output}"
  [ "${output_array[@]}" == "${expected_output}" ] || return 1 && return 0
}

test_find_gradle_property_handles_spaces(){
  expected_output="-Pkey=a name"
  local -a output_array
  find_gradle_property release.sh 1.0.0 "${expected_output}"
  printf "Expected \"%s\", got \"%s\"" "${expected_output}" "${output}"
  [ "${output_array[@]}" == "${expected_output}" ] || return 1 && return 0
}

test_properties_are_passed_on_correctly(){
  local -a properties
  find_gradle_property properties release.sh 1.0.0 -Pkey="a value" -Pkey2=value2
  function count_arguments(){
    echo $#
  }
  actual_argument_number=$(count_arguments "${properties[@]}")
  expected_argument_number=2
  printf "Expected %d arguments, got %d" "${expected_argument_number}" "${actual_argument_number}"
  [ "${actual_argument_number}" -eq "${expected_argument_number}" ] || return 1 && return 0

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

run_tests "test_find_gradle_property_extracts_property" \
"test_find_gradle_property_handles_spaces" \
"test_properties_are_passed_on_correctly" \
"test_find_gradle_property_check_fails_if_variable_is_undeclared" \
"test_find_gradle_property_check_fails_if_variable_is_not_an_array" \
"test_find_gradle_property_writes_to_array_variable"
