#!/usr/bin/env bash

SCRIPT_LOCATION=$(dirname -- "$(readlink -f -- "${BASH_SOURCE[0]}")")
# shellcheck source=./release_functions.sh
source "${SCRIPT_LOCATION}/../source_files/release_functions.sh"
source "${SCRIPT_LOCATION}/test_runner.sh"

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
  find_gradle_property output_array release.sh 1.0.0 "${expected_output}"
  printf "Expected \"%s\", got \"%s\"" "${expected_output}" "${output}"
  [ "${output_array[@]}" == "${expected_output}" ] || return 1 && return 0
}

test_find_gradle_property_handles_spaces(){
  expected_output="-Pkey=a name"
  local -a output_array
  find_gradle_property output_array release.sh 1.0.0 "${expected_output}"
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

test_single_quote_replacer_replaces_single_quotes(){
  local actual=$(replace_single_by_double_quotes "{'a':'b',
  'c':'d'}")
  local expected='{"a":"b",
  "c":"d"}'
  printf "Expected string'%s', got %s" "${expected}" "${actual}"
  [ "${actual}" == "${expected}" ] || return 1 && return 0
}

test_single_quote_replacer_does_not_touch_string_without_single_quotes(){
  local actual=$(replace_single_by_double_quotes 'this is a "test" string')
  local expected='this is a "test" string'
  printf "Expected string '%s', got '%s'" "${expected}" "${actual}"
  [ "${actual}" == "${expected}" ] || return 1 && return 0
}

test_determine_is_draft_prints_false_if_input_is_true(){
  local actual=$(determine_is_draft "true")
  local expected="false"
  printf "Expected string '%s', got '%s'" "${expected}" "${actual}"
  [ "${actual}" == "${expected}" ] || return 1 && return 0
}

test_determine_is_draft_prints_true_if_input_is_false(){
  local actual=$(determine_is_draft "false")
  local expected="true"
  printf "Expected string '%s', got '%s'" "${expected}" "${actual}"
  [ "${actual}" == "${expected}" ] || return 1 && return 0
}

test_determine_is_draft_prints_true_if_input_is_empty(){
  local actual=$(determine_is_draft "")
  local expected="true"
  printf "Expected string '%s', got '%s'" "${expected}" "${actual}"
  [ "${actual}" == "${expected}" ] || return 1 && return 0
}

test_missing_version_fails_check(){
 verify_version_present_and_formatted
 local actual_return_value=$?
 printf "Expected return value to be greater than zero, got '%d'" "${actual_return_value}"
 [ ${actual_return_value} -gt 0 ] || return 1 && return 0
}

test_malformed_version_fails_check(){
 verify_version_present_and_formatted "01.0X.1"
 local actual_return_value=$?
 printf "Expected return value to be greater than zero, got '%d'" "${actual_return_value}"
 [ ${actual_return_value} -gt 0 ] || return 1 && return 0
}

test_shortened_version_fails_check(){
 verify_version_present_and_formatted "1.1"
 local actual_return_value=$?
 printf "Expected return value to be greater than zero, got '%d'" "${actual_return_value}"
 [ ${actual_return_value} -gt 0 ] || return 1 && return 0
}

test_well_formed_version_passes_check(){
 verify_version_present_and_formatted "1.00.1-Whatever"
 local actual_return_value=$?
 printf "Expected return value to be zero, got '%d'" "${actual_return_value}"
 [ ${actual_return_value} -gt 0 ] || return 1 && return 0
}

test_a_release_version_passes_check(){
 verify_version_present_and_formatted "1.1.1"
 local actual_return_value=$?
 printf "Expected return value to be zero, got '%d'" "${actual_return_value}"
 [ ${actual_return_value} -eq 0 ] || return 1 && return 0
}

run_tests "test_find_gradle_property_extracts_property" \
"test_find_gradle_property_handles_spaces" \
"test_properties_are_passed_on_correctly" \
"test_find_gradle_property_check_fails_if_variable_is_undeclared" \
"test_find_gradle_property_check_fails_if_variable_is_not_an_array" \
"test_find_gradle_property_writes_to_array_variable" \
"test_single_quote_replacer_does_not_touch_string_without_single_quotes" \
"test_single_quote_replacer_replaces_single_quotes" \
"test_determine_is_draft_prints_false_if_input_is_true" \
"test_determine_is_draft_prints_true_if_input_is_false" \
"test_determine_is_draft_prints_true_if_input_is_empty" \
"test_missing_version_fails_check" \
"test_malformed_version_fails_check" \
"test_shortened_version_fails_check" \
"test_well_formed_version_passes_check" \
"test_a_release_version_passes_check"
exit $?
