v0.4.0
* made scenarios and cases in HTML reports collapsible (Issue-#18)
* slightly changed layout and appearance of HTML reports
* measure duration of each step and include in reports (Issue-#13)
* scenarios are sorted by name in HTML reports
* fix issue with intercepting methods of stages during construction
* fix issue when multiple exceptions are thrown to throw the first one instead of the last one
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
