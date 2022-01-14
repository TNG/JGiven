# reportportal-jgiven-example-tests
Test in junit5 and jgiven that demonstrates that import of jgiven tests run with junit5 into reportportal does not work

it uses standard reportportal demo to demonstrate, no setup needed.

First make sure that the src/test/resources/reportportal.properties contains correct values - especially uuid. 
You can see it at https://web.demo.reportportal.io/ui/#user-profile

use with java11 (for java8, modify build.gradle, behavior is the same)

Then run ./gradlew build 

it runs 4 junit5 tests and 2 jgiven tests

junit 2 tests are skipped, 1 pass, 1 fail
jgiven 1 pass, 1 fail

however, the imported results in reportportal are wrong: https://web.demo.reportportal.io/ui/#default_personal/launches/all

go to latest launch called junit5-and-jgiven-tests

you can see, that junit5 tests are imported correctly, but both jgiven test are passed


