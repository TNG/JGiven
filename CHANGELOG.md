# v0.5.0 (not released yet)

## New Features

### Derived Parameters [#15](https://github.com/TNG/JGiven/issues/15)

Derived parameters are parameters of step methods that are not directly passed
to the scenario as a parameter, but are derived from them.

Consider the following example that tests that a simple calculator can add 1 to some input number:

```
@Test
@DataProvider( { "0", "1", "3" })
public void the_calculator_should_be_able_to_add_one(int input) {
   given().a_calculator()
   when().adding_$_and_$( input, 1 );
   then().the_result_is( input + 1 );
}
```

The parameter of the last step `the_result_is` is not explicitly given as a parameter of the test method.
Instead it is just derived from the input. In v0.4.0, JGiven would then not be able to generate a data table
and fall back to print every case individually in the report.
In v0.5.0, JGiven will treat this derived parameter just like a normal parameter and will generate a data table
for it. The name of the placeholder is then taken from parameter name of the step method.
In addition, if an explicitly parameter is not used at all (but only derived values)
it will not even appear in the data table.

### Highlighting of case differences [#14](https://github.com/TNG/JGiven/issues/14)

If multiple cases of the same scenario are structurally different, JGiven cannot generate a data table for them.
Instead each case is printed individually in the report.
The problem is that the differences between the cases are often hard to spot.
To help the reader, the differences are now highlighted in the HTML report.

## Smaller enhancements

* Show number of cases in the scenario headline of the HTML report [#26](https://github.com/TNG/JGiven/issues/26)
* Elapsed time in HTML report should be shown in a convenient unit [#24](https://github.com/TNG/JGiven/issues/24)
* Test against different JUnit versions [#22](https://github.com/TNG/JGiven/issues/22)

## Fixed Bugs

* JGiven creates null.json files for test classes where all tests are ignored [#25](https://github.com/TNG/JGiven/issues/25)
* Printed reports should not have collapsed scenarios [#23](https://github.com/TNG/JGiven/issues/23)

## Backwards incompatible changes

### JSON Model

* The JSON model has been changed to support [#15](https://github.com/TNG/JGiven/issues/15).
  This means that JSON models generated with v0.4.0 will not work with the report generator of v0.5.0.
  This is in general no problem, because new JSON files are generated each time you execute your tests.

# v0.4.0
* made scenarios and cases in HTML reports collapsible [#18](https://github.com/TNG/JGiven/issues/18)
* slightly changed layout and appearance of HTML reports
* measure duration of each step and include in reports [#13](https://github.com/TNG/JGiven/issues/13)
* scenarios are sorted by name in HTML reports
* fix issue with intercepting methods of stages during construction
* fix issue when multiple exceptions are thrown to throw the first one instead of the last one
* @ScenarioDescription is now deprecated, instead just use @Description [#16](https://github.com/TNG/JGiven/issues/16)
* @Description now also works on test classes
* fixed case generation for parameterized JUnit runner [#21](https://github.com/TNG/JGiven/issues/21)

# v0.3.0
* Arguments from JUnit Parameterized and JUnitParams runner can now be read
* Arguments from JUnit Dataprovider runner are now read directly instead of parsing them
* Schritte-class is deprecated and has been replaced with Stufe-class (only relevant for german scenarios)
* the HTML report now escapes HTML in step arguments [#9](https://github.com/TNG/JGiven/issues/9)
* print style of HTML report is nicer
* steps following failed steps are now reported as skipped
* @NotImplementedYet annotation has new attributes failIfPass and executeSteps [#4](https://github.com/TNG/JGiven/issues/4)
