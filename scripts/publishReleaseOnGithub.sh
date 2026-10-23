#!/usr/bin/env bash

# Do not trace authentication, even when invoked with bash -x.
set +x
set -euo pipefail

fail(){
  printf 'Error: %s\n' "$1" >&2
  exit 1
}

version=
credentials=
for var in "$@"; do
  case "${var}" in
    --version=*)
      [[ -z "${version}" ]] || fail "Provide --version only once."
      version=${var#--version=}
      [[ -n "${version}" ]] || fail "--version must not be empty."
      ;;
    --credentials=*)
      printf 'Warning: --credentials is deprecated; use RELEASE_TOKEN or GITHUB_TOKEN instead.\n' >&2
      credentials=${var#--credentials=}
      [[ -n "${credentials}" ]] || fail "--credentials must not be empty."
      ;;
    *)
      fail "Unknown or malformed option. Usage: $0 --version=<x.y.z> (set RELEASE_TOKEN or GITHUB_TOKEN)."
      ;;
  esac
done

[[ -n "${version}" ]] || fail "Missing --version=<x.y.z> (without the v prefix)."
[[ "${version}" =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[A-Za-z0-9]+([.-][A-Za-z0-9]+)*)?$ ]] \
  || fail "Invalid version: expected x.y.z or x.y.z-suffix, without the v prefix."
for dependency in curl jq; do
  command -v "${dependency}" >/dev/null || fail "Required command not found: ${dependency}."
done

token=${RELEASE_TOKEN:-${GITHUB_TOKEN:-}}
if [[ -n "${token}" ]]; then
  [[ "${token}" != *$'\n'* && "${token}" != *$'\r'* ]] || fail "Token must not contain newlines."
  printf -v authorization '%s %s' Bearer "${token}"
elif [[ -n "${credentials}" ]]; then
  authorization="Basic $(printf '%s' "${credentials}" | base64 | tr -d '\n')"
else
  fail "Set RELEASE_TOKEN or GITHUB_TOKEN with permission to publish releases."
fi

umask 077
response_file=$(mktemp)
trap 'rm -f "${response_file}"' EXIT
api_url=https://api.github.com/repos/TNG/JGiven/releases
release_tag="v${version}"

request(){
  # Read authentication from stdin, not curl's process arguments.
  if ! status=$(printf 'Authorization: %s\n' "${authorization}" |
    curl --disable --silent --show-error --header @- \
      --header 'Accept: application/vnd.github+json' \
      --output "${response_file}" --write-out '%{http_code}' "$@"); then
    printf 'GitHub API request failed (HTTP %s). Response:\n' "${status:-unknown}" >&2
    cat "${response_file}" >&2
    exit 1
  fi
}

api_error(){
  printf 'GitHub API returned HTTP %s while %s:\n' "${status}" "$1" >&2
  cat "${response_file}" >&2
  exit 1
}

check_existing_release(){
  if jq -e '.draft == false' "${response_file}" >/dev/null; then
    printf 'Release %s is already published; nothing to do.\n' "${release_tag}"
    exit 0
  elif jq -e '.draft == true' "${response_file}" >/dev/null; then
    fail "Release ${release_tag} already exists as a draft. Publish or delete it manually before rerunning."
  else
    fail "Invalid release response from GitHub (expected a boolean draft field)."
  fi
}

request "${api_url}/tags/${release_tag}"
case "${status}" in
  200) check_existing_release ;;
  404) ;;
  *) api_error "looking up ${release_tag}" ;;
esac

# The tag endpoint only promises published releases. List releases to find drafts.
page=1
while :; do
  request "${api_url}?per_page=100&page=${page}"
  [[ "${status}" == 200 ]] || api_error "checking for draft releases"
  jq -e 'type == "array"' "${response_file}" >/dev/null \
    || fail "Invalid release list response from GitHub."
  existing=$(jq -c --arg tag "${release_tag}" '.[] | select(.tag_name == $tag)' "${response_file}")
  if [[ -n "${existing}" ]]; then
    printf '%s\n' "${existing}" > "${response_file}"
    check_existing_release
  fi
  [[ $(jq 'length' "${response_file}") -eq 100 ]] || break
  page=$((page + 1))
done

is_draft=true
[[ "${RELEASE:-}" != true ]] || is_draft=false
body=$(jq -n --arg tag "${release_tag}" --arg version "${version}" --argjson draft "${is_draft}" '{
  tag_name: $tag,
  name: ("Release " + $tag),
  body: ("Version " + $version + " release of JGiven.\nSource code may be found in Maven Central: https://search.maven.org/search?q=com.tngtech.jgiven"),
  draft: $draft
}')
request --fail-with-body --request POST --header 'Content-Type: application/json' \
  --data "${body}" "${api_url}"
[[ "${status}" == 201 ]] || api_error "creating ${release_tag}"
printf 'Created release %s (draft=%s).\n' "${release_tag}" "${is_draft}"
