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
    /^# / {
      if (found) exit
      if ($0 == heading) found = 1
      next
    }
    found {
      if ($0 !~ /^[[:space:]]*$/) has_content = 1
      if (has_content) print
    }
    END { if (!has_content) exit 1 }
  ' "${changelog_file}" || {
    printf 'Error: No changelog content found for version %s in %s.\n' "${version}" "${changelog_file}" >&2
    return 1
  }
}
