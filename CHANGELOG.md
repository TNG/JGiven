v0.4.0
* fix issue with intercepting methods of stages during construction
* if multiple exceptions are thrown during scenario execution throw the first one at the end of the test execution
* made scenarios and cases in HTML reports collapsible (Issue-#18)
* scenarios are sorted by name in HTML reports
* @ScenarioDescription is now deprecated, instead just use @Description (Issue-#16)
* @Description now also works on test classes
* fixed case generation for parameterized JUnit runner (Issue-#21)

v0.3.0
* Arguments from JUnit Parameterized and JUnitParams runner can now be read
* Arguments from JUnit Dataprovider runner are now read directly instead of parsing them
* Schritte-class is deprecated and has been replaced with Stufe-class (only relevant for german scenarios)
* the HTML report now escapes HTML in step arguments (Issue-#9)
* print style of HTML report is nicer
* steps following failed steps are now reported as skipped
* @NotImplementedYet annotation has new attributes failIfPass and executeSteps (Issue-#4)

