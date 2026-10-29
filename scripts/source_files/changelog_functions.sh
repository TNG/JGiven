#!/usr/bin/env bash

function changelog_content_for_version(){
  if [ $# -ne 2 ]; then
    printf 'Error: Expected a version and changelog file.\n' >&2
    return 1
  fi
  local version="$1"
  local changelog_file="$2"
  if [[ ! -r "${changelog_file}" ]]; then
    printf 'Error: Cannot read changelog: %s\n' "${changelog_file}" >&2
    return 1
  fi

  awk -v heading="# Release v${version}" '
    { sub(/\r$/, "") }
    /^# / && found { exit }
    heading == $0 { found = 1 ; next }
    found && ! /^[[:space:]]*$/ { has_content = 1 }
    has_content { print }
    END { exit !has_content }
  ' "${changelog_file}" || {
    printf 'Error: No changelog content found for version %s in %s.\n' "${version}" "${changelog_file}" >&2
    return 1
  }
}
