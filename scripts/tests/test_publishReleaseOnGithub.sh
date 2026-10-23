#!/usr/bin/env bash
# Functions are invoked indirectly by run_tests or exported to a subprocess.
# shellcheck disable=SC2317

SCRIPT_LOCATION=$(dirname -- "$(readlink -f -- "${BASH_SOURCE[0]}")")
# shellcheck source=scripts/tests/test_runner.sh
source "${SCRIPT_LOCATION}/test_runner.sh"

test_directory=$(mktemp -d)
trap 'rm -rf "${test_directory}"' EXIT
export test_directory

# Export a mock into the script's subprocess so no test accesses GitHub.
curl(){
  local output='' method=GET url='' strict=false payload=''
  printf '%s\n' "$@" >> "${test_directory}/arguments"
  cat >> "${test_directory}/headers"
  while [[ $# -gt 0 ]]; do
    case "$1" in
      --output) output=$2; shift ;;
      --request) method=$2; shift ;;
      --data) payload=$2; shift ;;
      --fail-with-body) strict=true ;;
      https://*) url=$1 ;;
    esac
    shift
  done
  printf '%s %s\n' "${method}" "${url}" >> "${test_directory}/requests"
  [[ "${MOCK_TRANSPORT_FAILURE:-false}" != true ]] || return 7
  local status response
  if [[ "${method}" == POST ]]; then
    [[ "${strict}" == true ]] || return 90
    printf '%s' "${payload}" > "${test_directory}/payload"
    status=${MOCK_CREATE_STATUS:-201}
    response='{"message":"create response"}'
  elif [[ "${url}" == */tags/* ]]; then
    status=${MOCK_LOOKUP_STATUS:-404}
    response=${MOCK_LOOKUP_BODY:-'{"message":"lookup response"}'}
  else
    status=${MOCK_LIST_STATUS:-200}
    response=${MOCK_LIST_BODY:-'[]'}
    if [[ "${MOCK_PAGINATION:-false}" == true ]]; then
      if [[ "${url}" == *page=1 ]]; then
        response=$(jq -nc '[range(100) | {tag_name: "v0.0.0", draft: false}]')
      else
        response='[{"tag_name":"v1.2.3","draft":true}]'
      fi
    fi
  fi
  printf '%s' "${response}" > "${output}"
  printf '%s' "${status}"
  [[ "${strict}" != true || "${status}" -lt 400 ]] || return 22
}
export -f curl

reset_mock(){
  rm -f "${test_directory:?}/"*
  unset MOCK_LOOKUP_STATUS MOCK_LOOKUP_BODY MOCK_LIST_STATUS MOCK_LIST_BODY
  unset MOCK_CREATE_STATUS MOCK_TRANSPORT_FAILURE MOCK_PAGINATION
  unset GITHUB_TOKEN RELEASE
  export RELEASE_TOKEN=example-release-token
}

invoke(){
  bash "${SCRIPT_LOCATION}/../publishReleaseOnGithub.sh" "$@" > "${test_directory}/output" 2>&1
}

assert_authorization(){
  local expected
  printf -v expected 'Authorization: %s %s' Bearer "$1"
  grep -Fxq "${expected}" "${test_directory}/headers"
}

test_invalid_options(){
  local option
  for option in '' '--version=' '--version' '--versionXYZ=1.2.3' '--version=v1.2.3' \
    '--version=..' '--version=1.2' '--version=1.2.3-' '--credentials=' '--unknown=secret-value'; do
    reset_mock
    if invoke ${option:+"${option}"}; then
      printf 'Expected invalid input to fail: %s\n' "${option}"
      return 1
    fi
    grep -q 'Error:' "${test_directory}/output" || return 1
    [[ ! -e "${test_directory}/requests" ]] || return 1
    ! grep -q 'secret-value' "${test_directory}/output" || return 1
  done
  reset_mock
  ! invoke --version=1.2.3 --version=2.0.0
}

test_missing_authentication(){
  reset_mock
  unset RELEASE_TOKEN
  ! invoke --version=1.2.3 &&
    grep -q 'Set RELEASE_TOKEN or GITHUB_TOKEN' "${test_directory}/output" &&
    [[ ! -e "${test_directory}/requests" ]]
}

test_create_release(){
  reset_mock
  export RELEASE=true
  invoke --version=1.2.3 || return 1
  jq -e '.tag_name == "v1.2.3" and .name == "Release v1.2.3" and .draft == false
    and (.body | contains("\nSource code"))' "${test_directory}/payload" &&
    grep -q -- '--fail-with-body' "${test_directory}/arguments" &&
    grep -q 'Accept: application/vnd.github+json' "${test_directory}/arguments" &&
    [[ $(wc -l < "${test_directory}/requests") -eq 3 ]] &&
    assert_authorization example-release-token &&
    ! grep -q 'example-release-token' "${test_directory}/arguments" "${test_directory}/output"
}

test_default_draft_and_github_token_fallback(){
  reset_mock
  unset RELEASE_TOKEN
  export GITHUB_TOKEN=example-fallback-token
  invoke --version=1.2.3-RC.1 &&
    jq -e '.draft == true and .tag_name == "v1.2.3-RC.1"' "${test_directory}/payload" &&
    assert_authorization example-fallback-token
}

test_legacy_credentials(){
  reset_mock
  unset RELEASE_TOKEN
  invoke --version=1.2.3 --credentials=example-user:example-password &&
    grep -q 'deprecated' "${test_directory}/output" &&
    grep -q "Authorization: Basic $(printf '%s' 'example-user:example-password' | base64)" "${test_directory}/headers" &&
    ! grep -q 'example-password' "${test_directory}/arguments" "${test_directory}/output"
}

test_environment_token_precedence(){
  reset_mock
  export GITHUB_TOKEN=example-fallback-token
  invoke --version=1.2.3 --credentials=example-user:example-password &&
    assert_authorization example-release-token &&
    ! grep -q 'example-fallback-token' "${test_directory}/headers"
}

test_existing_published_release(){
  reset_mock
  export MOCK_LOOKUP_STATUS=200 MOCK_LOOKUP_BODY='{"draft":false}'
  invoke --version=1.2.3 &&
    grep -q 'already published' "${test_directory}/output" &&
    [[ $(wc -l < "${test_directory}/requests") -eq 1 ]]
}

test_existing_draft_release(){
  reset_mock
  export MOCK_LOOKUP_STATUS=200 MOCK_LOOKUP_BODY='{"draft":true}'
  ! invoke --version=1.2.3 &&
    grep -q 'Publish or delete it manually' "${test_directory}/output" &&
    [[ $(wc -l < "${test_directory}/requests") -eq 1 ]]
}

test_draft_found_in_list(){
  reset_mock
  export MOCK_LIST_BODY='[{"tag_name":"v1.2.3","draft":true}]'
  ! invoke --version=1.2.3 &&
    grep -q 'already exists as a draft' "${test_directory}/output" &&
    [[ ! -e "${test_directory}/payload" ]]
}

test_paginated_draft_lookup(){
  reset_mock
  export MOCK_PAGINATION=true
  ! invoke --version=1.2.3 &&
    grep -q 'already exists as a draft' "${test_directory}/output" &&
    grep -q 'page=2' "${test_directory}/requests" &&
    [[ ! -e "${test_directory}/payload" ]]
}

test_api_failures(){
  local status stage
  for stage in LOOKUP LIST CREATE; do
    for status in 301 401 403 422 500; do
      reset_mock
      export "MOCK_${stage}_STATUS=${status}"
      if invoke --version=1.2.3; then
        printf 'Expected %s HTTP %s to fail\n' "${stage}" "${status}"
        return 1
      fi
      grep -q 'GitHub API' "${test_directory}/output" || return 1
      if [[ "${stage}" != CREATE ]]; then
        [[ ! -e "${test_directory}/payload" ]] || return 1
      fi
    done
  done
  reset_mock
  export MOCK_TRANSPORT_FAILURE=true
  ! invoke --version=1.2.3 &&
    grep -q 'GitHub API request failed' "${test_directory}/output" &&
    [[ ! -e "${test_directory}/payload" ]]
}

test_malformed_responses(){
  local response
  for response in 'invalid JSON' '{}' '{"draft":"false"}'; do
    reset_mock
    export MOCK_LOOKUP_STATUS=200 MOCK_LOOKUP_BODY="${response}"
    ! invoke --version=1.2.3 || return 1
    [[ ! -e "${test_directory}/payload" ]] || return 1
  done
  reset_mock
  export MOCK_LIST_BODY='{}'
  ! invoke --version=1.2.3 && [[ ! -e "${test_directory}/payload" ]]
}

test_token_header_injection(){
  reset_mock
  export RELEASE_TOKEN=$'example-token\nInjected: header'
  ! invoke --version=1.2.3 &&
    grep -q 'must not contain newlines' "${test_directory}/output" &&
    [[ ! -e "${test_directory}/requests" ]]
}

run_tests test_invalid_options test_missing_authentication test_create_release \
  test_default_draft_and_github_token_fallback test_legacy_credentials \
  test_environment_token_precedence test_existing_published_release \
  test_existing_draft_release test_draft_found_in_list test_paginated_draft_lookup \
  test_api_failures test_malformed_responses test_token_header_injection
exit $?
