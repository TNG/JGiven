#!/usr/bin/env bash
set -euo pipefail

SCRIPT_LOCATION=$(dirname -- "$(readlink -f -- "${BASH_SOURCE[0]}")")

poms_in_publication_folder=$(find ${SCRIPT_LOCATION}/../ -name pom-default.xml | grep publications)
failed_validations=""
for pom in ${poms_in_publication_folder};do
  if mvn validate -q -f "${pom}";then
    continue
  else
    failed_validations="${failed_validations:-""} Validation failed for POM file '${pom}'\n"
  fi
done

if [ -n "${failed_validations}" ];then
  printf "${failed_validations}"
  exit 1
fi
