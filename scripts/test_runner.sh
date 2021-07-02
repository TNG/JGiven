run_tests(){
  if [ $# -lt 1 ];then
    printf "No tests to run!"
    return 1
  fi
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
  return $( test ${failed_tests} -eq 0; echo $?)
}
