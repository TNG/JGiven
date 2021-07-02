#!/usr/bin/env bash

#Update the version property in a gradle properties file
function update_version(){
  [ $# -eq 2 ] || return 11
  local target_version="$1"
  local target_file="$2"
  local version_property="version"
  local version_matcher="${version_property}=.*"
  if grep -E "${version_matcher}" "${target_file}" ;then
    sed -i -e "s/${version_matcher}/${version_property}=${target_version}/" "${target_file}"
  else
    printf "Could not find version property to replace in file %s\n" "${target_file}"
    return 12
  fi
  return 0
}