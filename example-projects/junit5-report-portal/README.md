# JUnit5 Report Portal Example Project

This project shows how JGiven can be used with ReportPortal and JUnit 5 tests

1. Run `../../gradlew build`
2. Open `build/reports/jgiven/test/html/index.html`

Take not that the order in which the respective Extensions are loaded is important. JGiven needs to be executed first, or else ReportPortal will have no Exception to report. Scenarios skipped with the `@Pending` annotation cannot be reported at the momemnt.