#!/usr/bin/env bash
# Functions are invoked indirectly by run_tests or exported to a subprocess.
# shellcheck disable=SC2317

SCRIPT_LOCATION=$(dirname -- "$(readlink -f -- "${BASH_SOURCE[0]}")")
# shellcheck source=scripts/tests/test_runner.sh
source "${SCRIPT_LOCATION}/test_runner.sh"
# shellcheck source=scripts/source_files/changelog_functions.sh
source "${SCRIPT_LOCATION}/../source_files/changelog_functions.sh"

test_directory=$(mktemp -d)
trap 'rm -rf "${test_directory}"' EXIT
export test_directory

# Export a mock into the script's subprocess so no test accesses GitHub.
curl(){
  local method=GET url=''
  mock_output=''
  mock_strict=false
  printf '%s\n' "$@" >> "${test_directory}/arguments"
  cat >> "${test_directory}/headers"
  while [[ $# -gt 0 ]]; do
    case "$1" in
      --output) mock_output=$2; shift ;;
      --request) method=$2; shift ;;
      --data) printf '%s' "$2" > "${test_directory}/payload"; shift ;;
      --fail-with-body) mock_strict=true ;;
      https://*) url=$1 ;;
    esac
    shift
  done
  printf '%s %s\n' "${method}" "${url}" >> "${test_directory}/requests"
  mock_response "${method} ${url}"
}

reply(){
  printf '%s' "$2" > "${mock_output}"
  printf '%s' "$1"
  [[ "${mock_strict}" != true || "$1" -lt 400 ]] || return 22
}

unexpected_request(){
  printf 'Unexpected curl request: %s\n' "$1" >&2
  touch "${test_directory}/unexpected_request"
  return 99
}
export -f curl reply unexpected_request

reset_mock(){
  rm -f "${test_directory:?}/"*
  mock_response(){ unexpected_request "$1"; }
  unset GITHUB_TOKEN RELEASE
  export RELEASE_TOKEN=example-release-token
}

invoke(){
  export -f mock_response
  local result=0
  bash "${SCRIPT_LOCATION}/../publishReleaseOnGithub.sh" "$@" > "${test_directory}/output" 2>&1 || result=$?
  if [[ -e "${test_directory}/unexpected_request" ]]; then
    cat "${test_directory}/output"
    exit 1
  fi
  return "${result}"
}

mock_successful_creation(){
  mock_response(){
    case "$1" in
      'GET https://api.github.com/repos/TNG/JGiven/releases?per_page=100&page=1') reply 200 '[]' ;;
      'POST https://api.github.com/repos/TNG/JGiven/releases')
        [[ "${mock_strict}" == true ]] || return 90
        reply 201 '{"message":"created"}'
        ;;
      *) unexpected_request "$1" ;;
    esac
  }
}

assert_authorization(){
  grep -Fxq "Authorization: Bearer $1" "${test_directory}/headers"
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
  mock_successful_creation
  export RELEASE=true
  invoke --version=1.2.3 || return 1
  jq -e '.tag_name == "v1.2.3" and .name == "Release v1.2.3" and .draft == false
    and (.body | contains("\nSource code"))
    and (.body | contains("## Fixed issues\n* Fixed build error"))
    and (.body | contains("#947"))
    and (.body | contains("#1200") | not)' "${test_directory}/payload" &&
    grep -q -- '--fail-with-body' "${test_directory}/arguments" &&
    grep -q 'Accept: application/vnd.github+json' "${test_directory}/arguments" &&
    [[ $(wc -l < "${test_directory}/requests") -eq 2 ]] &&
    assert_authorization example-release-token &&
    ! grep -q 'example-release-token' "${test_directory}/arguments" "${test_directory}/output"
}

test_default_draft_and_github_token_fallback(){
  reset_mock
  mock_successful_creation
  unset RELEASE_TOKEN
  export GITHUB_TOKEN=example-fallback-token
  invoke --version=1.2.3 &&
    jq -e '.draft == true and .tag_name == "v1.2.3"' "${test_directory}/payload" &&
    assert_authorization example-fallback-token
}

test_legacy_credentials(){
  reset_mock
  mock_successful_creation
  unset RELEASE_TOKEN
  invoke --version=1.2.3 --credentials=example-user:example-password &&
    grep -q 'deprecated' "${test_directory}/output" &&
    grep -q "Authorization: Basic $(printf '%s' 'example-user:example-password' | base64)" "${test_directory}/headers" &&
    ! grep -q 'example-password' "${test_directory}/arguments" "${test_directory}/output"
}

test_environment_token_precedence(){
  reset_mock
  mock_successful_creation
  export GITHUB_TOKEN=example-fallback-token
  invoke --version=1.2.3 --credentials=example-user:example-password &&
    assert_authorization example-release-token &&
    ! grep -q 'example-fallback-token' "${test_directory}/headers"
}

test_existing_published_release(){
  reset_mock
  mock_response(){
    case "$1" in
      'GET https://api.github.com/repos/TNG/JGiven/releases?per_page=100&page=1')
        reply 200 '[{"tag_name":"v1.2.3","draft":false}]' ;;
      *) unexpected_request "$1" ;;
    esac
  }
  invoke --version=1.2.3 &&
    grep -q 'already published' "${test_directory}/output" &&
    [[ $(wc -l < "${test_directory}/requests") -eq 1 ]]
}

test_existing_draft_release(){
  reset_mock
  mock_response(){
    case "$1" in
      'GET https://api.github.com/repos/TNG/JGiven/releases?per_page=100&page=1')
        reply 200 '[{"tag_name":"v1.2.3","draft":true}]' ;;
      *) unexpected_request "$1" ;;
    esac
  }
  ! invoke --version=1.2.3 &&
    grep -q 'Publish or delete it manually' "${test_directory}/output" &&
    [[ $(wc -l < "${test_directory}/requests") -eq 1 ]]
}

test_paginated_published_lookup(){
  reset_mock
  mock_response(){
    case "$1" in
      'GET https://api.github.com/repos/TNG/JGiven/releases?per_page=100&page=1')
        reply 200 "$(jq -nc '[range(100) | {tag_name: "v0.0.0", draft: false}]')" ;;
      'GET https://api.github.com/repos/TNG/JGiven/releases?per_page=100&page=2')
        reply 200 '[{"tag_name":"v1.2.3","draft":false}]' ;;
      *) unexpected_request "$1" ;;
    esac
  }
  invoke --version=1.2.3 &&
    grep -q 'already published' "${test_directory}/output" &&
    [[ $(wc -l < "${test_directory}/requests") -eq 2 ]] &&
    [[ ! -e "${test_directory}/payload" ]]
}

test_paginated_lookup_failure(){
  reset_mock
  mock_response(){
    case "$1" in
      'GET https://api.github.com/repos/TNG/JGiven/releases?per_page=100&page=1')
        reply 200 "$(jq -nc '[range(100) | {tag_name: "v0.0.0", draft: false}]')" ;;
      'GET https://api.github.com/repos/TNG/JGiven/releases?per_page=100&page=2')
        reply 500 '{"message":"server error"}' ;;
      *) unexpected_request "$1" ;;
    esac
  }
  ! invoke --version=1.2.3 &&
    grep -q 'HTTP 500' "${test_directory}/output" &&
    [[ $(wc -l < "${test_directory}/requests") -eq 2 ]] &&
    [[ ! -e "${test_directory}/payload" ]]
}

test_paginated_draft_lookup(){
  reset_mock
  mock_response(){
    case "$1" in
      'GET https://api.github.com/repos/TNG/JGiven/releases?per_page=100&page=1')
        reply 200 "$(jq -nc '[range(100) | {tag_name: "v0.0.0", draft: false}]')" ;;
      'GET https://api.github.com/repos/TNG/JGiven/releases?per_page=100&page=2')
        reply 200 '[{"tag_name":"v1.2.3","draft":true}]' ;;
      *) unexpected_request "$1" ;;
    esac
  }
  ! invoke --version=1.2.3 &&
    grep -q 'already exists as a draft' "${test_directory}/output" &&
    grep -q 'page=2' "${test_directory}/requests" &&
    [[ ! -e "${test_directory}/payload" ]]
}

test_api_failures(){
  local status stage
  for stage in LIST CREATE; do
    for status in 301 401 403 404 422 500; do
      reset_mock
      export test_http_status="${status}" test_failure_stage="${stage}"
      mock_response(){
        case "$1" in
          'GET https://api.github.com/repos/TNG/JGiven/releases?per_page=100&page=1')
            if [[ "${test_failure_stage}" == LIST ]]; then
              reply "${test_http_status}" '{"message":"list failure"}'
            else
              reply 200 '[]'
            fi ;;
          'POST https://api.github.com/repos/TNG/JGiven/releases')
            [[ "${test_failure_stage}" == CREATE ]] || { unexpected_request "$1"; return $?; }
            reply "${test_http_status}" '{"message":"create failure"}' ;;
          *) unexpected_request "$1" ;;
        esac
      }
      if invoke --version=1.2.3; then
        printf 'Expected %s HTTP %s to fail\n' "${stage}" "${status}"
        return 1
      fi
      grep -q 'GitHub API' "${test_directory}/output" || return 1
      grep -q "HTTP ${status}" "${test_directory}/output" || return 1
      if [[ "${stage}" != CREATE ]]; then
        [[ ! -e "${test_directory}/payload" ]] || return 1
      fi
    done
  done
  reset_mock
  mock_response(){
    case "$1" in
      'GET https://api.github.com/repos/TNG/JGiven/releases?per_page=100&page=1') return 7 ;;
      *) unexpected_request "$1" ;;
    esac
  }
  ! invoke --version=1.2.3 &&
    grep -q 'GitHub API request failed' "${test_directory}/output" &&
    [[ ! -e "${test_directory}/payload" ]]
}

test_malformed_responses(){
  local response
  local -a responses=('invalid JSON' '{}' '[{"tag_name":"v1.2.3"}]' '[{"tag_name":"v1.2.3","draft":"false"}]')
  for response in "${responses[@]}"; do
    reset_mock
    export response
    mock_response(){
      case "$1" in
        'GET https://api.github.com/repos/TNG/JGiven/releases?per_page=100&page=1') reply 200 "${response}" ;;
        *) unexpected_request "$1" ;;
      esac
    }
    ! invoke --version=1.2.3 || return 1
    [[ ! -e "${test_directory}/payload" ]] || return 1
  done
}

test_token_header_injection(){
  reset_mock
  export RELEASE_TOKEN=$'example-token\nInjected: header'
  ! invoke --version=1.2.3 &&
    grep -q 'must not contain newlines' "${test_directory}/output" &&
    [[ ! -e "${test_directory}/requests" ]]
}

test_changelog_section_selection(){
  reset_mock
  cat > "${test_directory}/CHANGELOG.md" <<'EOF'
# Release v1.2.30
Wrong version
# Release v1.2.3

## Fixed issues
* Preserve "quotes", 'apostrophes', \backslashes and `code`.

  Nested paragraph with [a link](https://example.com).

# Release v1.2.2
Older version
EOF
  local expected actual
  expected=$'## Fixed issues\n* Preserve "quotes", \'apostrophes\', \\backslashes and `code`.\n\n  Nested paragraph with [a link](https://example.com).'
  actual=$(changelog_content_for_version 1.2.3 "${test_directory}/CHANGELOG.md") || return 1
  [[ "${actual}" == "${expected}" ]] || return 1
  actual=$(changelog_content_for_version 1.2.2 "${test_directory}/CHANGELOG.md") || return 1
  [[ "${actual}" == 'Older version' ]]
}

test_changelog_missing_or_empty(){
  reset_mock
  mock_response(){
    case "$1" in
      'GET https://api.github.com/repos/TNG/JGiven/releases?per_page=100&page=1') reply 200 '[]' ;;
      *) unexpected_request "$1" ;;
    esac
  }
  ! changelog_content_for_version 1.2.3 "${test_directory}/missing.md" || return 1
  printf '# Release v1.2.3\n\n \n# Release v1.2.2\nOlder version\n' > "${test_directory}/CHANGELOG.md"
  ! changelog_content_for_version 1.2.3 "${test_directory}/CHANGELOG.md" || return 1
  ! changelog_content_for_version 9.9.9 "${test_directory}/CHANGELOG.md" || return 1
  ! invoke --version=99999.0.0 &&
    grep -q 'No changelog content found for version 99999.0.0' "${test_directory}/output" &&
    [[ ! -e "${test_directory}/payload" ]]
}

test_changelog_prerelease_and_crlf(){
  reset_mock
  printf '# Release v1.2.3-RC.1\r\n\r\n## Changes\r\n* Prerelease notes\r\n' > "${test_directory}/CHANGELOG.md"
  local actual
  actual=$(changelog_content_for_version 1.2.3-RC.1 "${test_directory}/CHANGELOG.md") || return 1
  [[ "${actual}" == $'## Changes\n* Prerelease notes' ]]
}

test_existing_release_without_changelog(){
  reset_mock
  mock_response(){
    case "$1" in
      'GET https://api.github.com/repos/TNG/JGiven/releases?per_page=100&page=1')
        reply 200 '[{"tag_name":"v99999.0.0","draft":false}]' ;;
      *) unexpected_request "$1" ;;
    esac
  }
  invoke --version=99999.0.0 &&
    grep -q 'already published' "${test_directory}/output" &&
    [[ ! -e "${test_directory}/payload" ]]
}

run_tests test_invalid_options test_missing_authentication test_create_release \
  test_default_draft_and_github_token_fallback test_legacy_credentials \
  test_environment_token_precedence test_existing_published_release \
  test_existing_draft_release test_paginated_published_lookup test_paginated_draft_lookup \
  test_paginated_lookup_failure \
  test_api_failures test_malformed_responses test_token_header_injection \
  test_changelog_section_selection test_changelog_missing_or_empty \
  test_changelog_prerelease_and_crlf test_existing_release_without_changelog
exit $?
