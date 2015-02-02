# v0.7.0 (not released yet)

## New Features

* Support for attachments on steps (Pull Request [#56](https://github.com/TNG/JGiven/pull/56))
* Descriptions for tags can be dynamically created (Issue [#55](https://github.com/TNG/JGiven/issues/55))
* Custom annotations can now be used to format arguments when they are annotated with the `@Format` annotation
* Introduced a new `@Quoted` annotation to surround arguments with double quotes (" ") in reports

## Changed Behavior
* String arguments are no longer put into single quotes (' ') when printed to the console

## Fixed Issues
* Methods annotated with `@Hidden` see now injected values from the previous stage if they are the first method called on a stage
* Fixes issue with IntelliJ JUnit runner that stays yellow when executing JGiven tests (Issue [#57](https://github.com/TNG/JGiven/issues/57))

# v0.6.2
## New Features

### Data tables as parameters
* Step parameters can now be annotated with `@Table`.
* Such parameters are stored as a table instead of a plain string in the report model.
* Reporters present these parameters as data tables instead of plain strings.
* The type of these parameters can be
  * A list of list (actually all types implementing Iterable are supported)
  * A two-dimensional array
  * A list or array of POJOs. In this case each POJO represents a single row and the fields of the POJOs are taken as columns

#### Example

##### POJO
```
class CoffeeWithPrice {
    String name;
    double price_in_EUR;
    CoffeeWithPrice(String name, double priceInEur) {
        this.name = name;
        this.price_in_EUR = priceInEur;
    }
}
```
#### The Step Method
```
public SELF the_prices_of_the_coffees_are( @Table CoffeeWithPrice... prices ) {
   ...
}
```
#### Invocation of the step method
```
   given().the_prices_of_the_coffees_are(
       new CoffeeWithPrice("Espresso", 2.0),
       new CoffeeWithPrice("Cappuccino", 2.5));
```
#### Text Report
```
   Given the prices of the coffees are

     | name       | price in EUR |
     +------------+--------------+
     | Espresso   | 2.0          |
     | Cappuccino | 2.5          |
```

#### Without POJO
The same effect can be achieved without a POJO by using a two-dimensional array or a list of list.
```
   given().the_prices_of_the_coffees_are( new Object[][] {
       { "name", "price in EUR" },
       { "Espresso", 2.0 },
       { "Cappuccino", 2.5}});
```

### Bookmarks in the HTML5 Report
The HTML5 report now allows you to make bookmarks of arbitrary pages. The bookmarks are stored in the local storage of the browser.

# v0.6.1
## New Features

### `@Hidden` annotation can be applied to step parameters
Step parameters can now be hidden in the report if they are annotated with the `@Hidden` annotation.

## Bug Fixes
* Core: Exceptions that are thrown after a scenario has been executed, e.g., in `@After`-annotated methods in JUnit, are not hiding the original exception thrown in the scenario (#49).
* HTML5 Report: Split the JSON model into multiple files to avoid `script too large` errors in Firefox (#51)
* HTML5 Report: Fixed issue with the encoding of tag URLs (#47)

# v0.6.0

## Major new Features

### HTML5 Report
There is a new HTML5 report that is completely written from scratch. Instead of mutliple static HTML pages it only consists of a single HTML page that dynamically loads its content from a single jsonp file that contains the model of the scenario report. As a result the overall size of the generated files is much smaller. In addition, the new report is based on Foundation, which is a modern CSS framework, so the new report also looks much nicer then the old static one. One of the main new features is also that the HTML5 report has a full text search built in that works without having to open all scenarios in a single HTML page. As it heavily relies on JavaScript, don't expect that it works on old Browsers.

## Bug Fixes
* Core: An issue has been fixed where methods annotated with `@AfterStage` and `@AfterScenario` could accidentally appear in the report when they have been overriden and not annotated again.
* Core: An issue has been fixed where methods annotated with `@AfterStage` and `@AfterScenario` have not been executed when an exception has been thrown (#46)

## Breaking Changes

### Non-public step methods appear in the report now
So far JGiven only reported public step methods. This behavior can lead to confusion, because it is not obvious that methods that are actually executed do not appear in the report. For this reason this behavior has been changed so that the visibility modifier is not taken into account anymore when reporting step methods.

#### How to migrate
If you have relied on the fact that non-public methods actually do not appear in the report, you can easily port your existing code by adding the `@Hidden` annotation to the corresponding method to explicitly state that the method should be hidden in the report.

### Removed deprecated `Schritte` class (only used for German scenarios)
Use `Stufe` class instead

### Removed `@CasesAsTable` annotation
As data tables are the default this annotation had no effect anymore, you can safely delete all usages.

### Removed `@ScenarioDescription` annotation
The annotation is now just called `@Description`.

# v0.5.4

tbd

# v0.5.3

## Minor new Features

* Text Report: Added status column to data tables [#34](https://github.com/TNG/JGiven/issues/34)
* HTML Report: Added expand all and collapse all buttons [#37](https://github.com/TNG/JGiven/issues/37)

## Bug Fixes

* HTML Report: Correctly show status of failed scenarios where all steps have been successful [#33](https://github.com/TNG/JGiven/issues/33)
* HTML Report: Fix issue where in certain cases the content of a scenario was not expandable (no issue number)

# v0.5.2

## New Features

### Core

* Step methods can now have extended descriptions with the `@ExtendedDescription` tag. The extended description can be shown in the HTML report. [#30](https://github.com/TNG/JGiven/issues/30)

### HTML Report

* The scenario content can now be searched
* A page with all scenarios is now generated

## Bug Fixes

* Fixed bug concerning duplicate parameters in data tables [#32](https://github.com/TNG/JGiven/issues/32)

# v0.5.1
## New Features

### HTML Report Improvements

* A page for all failed scenarios is now generated [#29](https://github.com/TNG/JGiven/issues/29)
* Classes and tags are shown in a hierarchical structure that can be searched [#28](https://github.com/TNG/JGiven/issues/28)
* The front page now contains statistics about the overall report instead of tags [#8](https://github.com/TNG/JGiven/issues/8)

# v0.5.0

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
