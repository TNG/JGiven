#!/usr/bin/env bash


SCRIPT_LOCATION=$(dirname -- "$(readlink -f -- "${BASH_SOURCE[0]}")")
# shellcheck source=./release_functions.sh
source "${SCRIPT_LOCATION}/source_files/release_functions.sh"
source "${SCRIPT_LOCATION}/source_files/helper_functions.sh"


function create_message_body(){
  verify_version_present_and_formatted $1 || return $?
  version="$1"
  release_tag="v$1"
  is_draft=$(determine_is_draft "${RELEASE}")
  json_adjacent_body="{
    'tag_name' : '${release_tag}',
    'name' : 'Release ${release_tag}',
    'body' : 'Version ${version} release of JGiven.\nSource code may be found in Maven Central: https://search.maven.org/search?q=com.tngtech.jgiven',
    'draft' : ${is_draft}
  }"
  replace_single_by_double_quotes "${json_adjacent_body}"
  return $?
}

for var in "$@";do
  case "${var}" in
    --version*)
        version=${var#--version=};;
    --credentials*)
      credentials=${var#--action_token=};;
    *)
      printf "Unknown option '%s' \n" "${1}"
      exit 1;;
  esac

done
[[ -n "${credentials}" ]] || { echo "Found no credentials to access github"; exit 2; }
body=$(create_message_body "$version") || { code=$?;  echo "Failed to create message body with error ${code}"; exit ${code}; }
curl -X POST \
  -H "Accept: application/vnd.github.v3+json" \
  https://api.github.com/repos/TNG/JGiven/releases \
  -u "${credentials}" \
  -d "${body}"
