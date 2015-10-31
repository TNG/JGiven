# v0.9.4

## Fixed Issues

* Blank values can now be formatted [#157](https://github.com/TNG/JGiven/issues/157)

## New Features

* Scenarios without steps can now be excluded from the report by using the new `--exclude-empty-scenarios` report generator option [#151](https://github.com/TNG/JGiven/issues/151)

# v0.9.3

## Fixed Issues

* HTML Report: Fixed an issue that inline attachments are shown multiple times [#145](https://github.com/TNG/JGiven/issues/145)
* HTML Report: fixed an issue with attachments that could lead to the generation of attachments with identical names, overriding attachments of other scenarios [#144](https://github.com/TNG/JGiven/issues/144)

## New Features

* Attachments can now have a custom filename [#144](https://github.com/TNG/JGiven/issues/144)
* HTML Report: allow arbitrary protocols in custom navigation links [#146](https://github.com/TNG/JGiven/issues/146)

# v0.9.2

## New Features

* Cases can now have custom descriptions by using the new annotation `@CaseDescription` [#139](https://github.com/TNG/JGiven/issues/139).
* Custom test class suffixes can now be configured using the `@AbstractJGivenConfiguration` annotation.

# v0.9.1

## New Features

* Global formatter now apply also to subtypes of the types they are configured for [#137](https://github.com/TNG/JGiven/issues/137)

## Fixed Issues

* Fixed an issue with TestNG that when using a data provider and one case fails the TestNG status was still reported as success. [#138](https://github.com/TNG/JGiven/issues/138)

# v0.9.0

## Backwards incompatible changes regarding placeholder variables
It is now not possible anymore to have spaces/underlines in placeholder variables. The rational behind this change is that the old behavior was hard to understand and did not really give much additional value.

### Old Behavior

The old behavior allowed placeholder variables to have spaces/underlines if the variable was terminated with another`$`. Consider the following example:
```
given().a_step_with_a_$placeholder_variable$_with_spaces( "foobar" );
```
Before v0.9.0 this resulted in the following report:

```
Given a step with a foobar with spaces
```
I.e. the complete string `$placeholder_variable$` was replaced. From v0.9.0 on this will not be possible anymore. In fact, the terminating `$` will be treated as a new placeholder variable.

### New Behavior
A placeholder variable must match the regular expression `\$[a-zA-Z0-9]*`. In particular, it cannot contain the `_` character. The above example, will now even throw a `JGivenWrongUsageException` because the number of arguments do not match the number of placeholders anymore.

### Migration
If you relied on the fact that placeholders can contains spaces or underlines, you have to rename your variables to camelCase. In addition, you have to remove the trailing `$`. So the above example becomes:

```
given().a_step_with_a_$placeholderVariable_with_spaces( "foobar" );
```

## New Features

* The name of the test class is now used by JGiven to group scenarios. The `@As` annotation can be used to define a different name. [#133](https://github.com/TNG/JGiven/issues/133)
* Scenarios can now have an extended description by annotated the test method with `@ExtendedDescription`. [#35](https://github.com/TNG/JGiven/issues/35)
* The stack trace of a failing scenario is now stored and shown in the HTML report [#95](https://github.com/TNG/JGiven/issues/95)
* Formatter for step parameters can now be defined by using the `@JGivenConfiguration` annotation on a test class.
* HTML Report: made title of the HTML report configurable using the new `--title` parameter of the report generator [#131](https://github.com/TNG/JGiven/issues/131)
* HTML Report: added possibility to provide a `custom.js` file that is included in the generated report. This allows you to further customize the report. [#129](https://github.com/TNG/JGiven/issues/129)
* HTML Report: made status circle clickable to show failed, pending, or successful scenarios [#130](https://github.com/TNG/JGiven/issues/130)

## Fixed Issues

* Special characters like parentheses are not treated as part of a placeholder name anymore [#118](https://github.com/TNG/JGiven/issues/118)

# v0.8.4

## Fixed Issues

* Fixed in issue with TestNG introduced with v0.8.3 that a failing case does not lead to failed TestNG status [#138](https://github.com/TNG/JGiven/issues/138)

# v0.8.3

## Fixed Issues

* Fixed an issue with TestNG that when using a data provider a failing case prevented following cases from finishing [#123](https://github.com/TNG/JGiven/issues/123)
* Fixed an issue that the status of the overall scenario could be successful although some cases of the scenario failed [#125](https://github.com/TNG/JGiven/issues/125)

# v0.8.2

## New Features

* Introduced the annotation `@DoNotIntercept`, to completely circumvent the JGiven interception mechanism of step methods. [#103](https://github.com/TNG/JGiven/issues/103)
* Introduced new feature to directly show image attachment in the HTML report. This is done by calling `showDirectly()` on the `Attachment` object. [#117](https://github.com/TNG/JGiven/issues/117)
* Introduced the possibility to automatically number the rows or columns of a table parameter. [#116](https://github.com/TNG/JGiven/issues/116)
* HTML Report: introduce pagination to better deal with large lists of scenarios. [#120](https://github.com/TNG/JGiven/pull/120)

## Fixed Issues

* HTML Report: fixed an issue that caused the scenario list to only shows the first 20 entries [#119](https://github.com/TNG/JGiven/issues/119)

# v0.8.1

## New Features

* Parameters of test methods can now also be formatted with formatters like step parameters. This is only relevant, however, when you have scenarios with multiple cases that do not generate a data table, but multiple cases [#114](https://github.com/TNG/JGiven/pull/114)

## Fixed Issues

* Fixed the issue that test classes had to be compiled with the -parameters option of javac when using Java 8. This was fixed by upgrading to the newest version of the Paranamer library that now fully supports Java 8 [#106](https://github.com/TNG/JGiven/pull/106)
* Fixed an issue where different formatted parameters having the same value in all cases were collapsed to a single parameter [#104](https://github.com/TNG/JGiven/issues/104)
* Fixed issue introduced with v0.8.0 that tag descriptions that differ depending on a tag value are not correctly reported
* Fixed an issue that the dataprovider with TestNG could not be used in parallel mode [#105](https://github.com/TNG/JGiven/issues/105)
* Fixed an issue that when using TestNG only a report for one test class was generated [#115](https://github.com/TNG/JGiven/issues/115)

# v0.8.0

## New Features

### Hierarchical Tags [#99](https://github.com/TNG/JGiven/pull/99)

Tags can now have parent tags by tagging a tag annotation. This allows you to define tag hierarchies.

#### Example

The following example tags the `FeatureHtml5Report` annotation with the `FeatureReport` annotation:

```
@FeatureReport
@IsTag( name = "HTML5 Report" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureHtml5Report { }
```

### Enhanced Spring Support [#94](https://github.com/TNG/JGiven/pull/94)

* The Spring support has been greatly improved. JGiven Stages can now be directly managed by the Spring framework, resulting in a much better Spring integration.
    * Note that the usage of Spring is optional and is provided by the `jgiven-spring` module.
* Introduced `@JGivenStage` to ease writing spring beans that act as JGiven stage
* Thanks to [TripleNail](https://github.com/TripleNail) for providing this enhancement!

### New features in the HTML5 Report

* Classes are shown now in hierarchical navigation tree and scenarios can be listed by package [#91](https://github.com/TNG/JGiven/pull/91)
* Durations are now shown in appropriate units instead of only showing them in seconds [#92](https://github.com/TNG/JGiven/issues/92)
* The navigation bar can now be hidden and resized [#96](https://github.com/TNG/JGiven/issues/96)
* Failed scenarios are now colored in red, pending scenarios are greyed out
* In the Summary section, the number of scenarios for each status are shown

### New `style` attribute for the `@IsTag` annotation

* The new `style` attribute allows you to define arbitrary inline styles for tags that will be applied to the tag in the HTML5 report

### JUnit: New `StandaloneScenarioRule`
* With this rule it is now easier to use JGiven without inheriting from `ScenarioTest`. Just put this rule as a `@Rule` and the `ScenarioReportRule` as a `@ClassRule` into your test class and you can use JGiven by injecting stages with `@ScenarioStage`.

## Removed Features

### Removed the old static HTML report generator [#101](https://github.com/TNG/JGiven/pull/101)
* As the HTML5 report supports all the features of the static HTML report, the static HTML report has been completely removed to avoid duplicate efforts when implementing new features.

## Fixed Issues

* HTML5 Report: fixed issue with duplicate entries in tables when used as step parameters [#89](https://github.com/TNG/JGiven/issues/89)
* HTML5 Report: fixed navigation and added searching in the mobile version
* HTML5 Report: fixed slow scrolling in case of large lists of scenarios
* Fixed an issue that the `@Description` annotation was not regarded for methods with the `@IntroWord` [#87](https://github.com/TNG/JGiven/issues/87)
* TestNG: fixed missing support for injection of stages into the test class using the `@ScenarioStage` annotation
* TestNG: fixed missing support for `@ScenarioState` annotation in test classes
* Removed unneeded ICU dependency

## New Annotations

* Introduced the `@As` annotation that replaces the `@Description` annotation when used on step methods and test methods. The `@Description` annotation should only be used for descriptions of test classes.
* Added `@Pending` annotation to replace the `@NotImplementedYet` annotation. [#100](https://github.com/TNG/JGiven/pull/100)

## Backwards incompatible JSON Model Changes

* The field `notImplementedYet` of the `ScenarioModel` was renamed to `pending`
* The `StepStatus` `NOT_IMPLEMENTED_YET` was renamed to `PENDING`.

Note: in general, backwards incompatible model changes should be no problem, as long as you use the same version for all JGiven modules (core, html5-report, maven-plugin).

# v0.7.3

## Fixed Issues

* Fixed major issue with Java 8 that prevented the usage of lambda expressions inside Stage classes [#85](https://github.com/TNG/JGiven/issues/85)
  Note that due to this fix you have to compile your test classes with the `-parameters` flag of javac if you are using Java 8.
* Fixed an issue in the HTML5 report which shows only attachments of the first case when having a parameterized scenario with multiple cases [#77](https://github.com/TNG/JGiven/issues/77)

## Changed Behavior

### Intro words are not necessary anymore [#74](https://github.com/TNG/JGiven/issues/74)

The following example is now a valid JGiven scenario, i.e. intro words are not required anymore:

```
given().frozen_strawberries()
   .a_banana()
   .milk();
when().mixing_everything_in_a_mixer();
then().you_get_a_delicious_smoothie();
```

The report will then look as follows:

```
 Given frozen_strawberries
       a banana
       milk
  When mixing everything in a mixer
  Then you get a delicious smoothie
```

In previous versions of JGiven you would have to add an `and()` before `a_banana()` and `milk()`

## New Features

* The HTML5 report now supports grouping, sorting, and filtering of result lists [PR #81](https://github.com/TNG/JGiven/pull/81)


# v0.7.2

## New Features

* Added `cssClass` and `color` attributes to `@IsTag` to customize tags in HTML reports [#69](https://github.com/TNG/JGiven/pull/69)

## Fixed Issues

* Custom CSS files are now copied to the target folder when generating HTML5 reports [#70](https://github.com/TNG/JGiven/issues/70)

# v0.7.1

## New Features

* Introduce `columnTitles` attribute for the `@Table` annotation [#64](https://github.com/TNG/JGiven/pull/64)
* Ignore `null` values of POJOs by default when using the `@Table` annotation. This behavior can be overridden with the `includeNullColumns` attribute.
* Allow $ to be escaped in step descriptions when using the `@Description` tag [#19](https://github.com/TNG/JGiven/pull/19)
* Speed-up the overall performance of JGiven by caching reflection-based look-ups

# v0.7.0

## New Features

* Support for attachments on steps (Pull Request [#56](https://github.com/TNG/JGiven/pull/56))
* Descriptions for tags can be dynamically created (Issue [#55](https://github.com/TNG/JGiven/issues/55))
* Custom annotations can now be used to format arguments when they are annotated with the `@Format` annotation
* Introduced a new `@Quoted` annotation to surround arguments with double quotes (" ") in reports

## Changed Behavior
* String arguments are no longer put into single quotes (' ') when printed to the console

## Fixed Issues
* Methods annotated with `@Hidden` see now injected values from the previous stage if they are the first method called on a stage
* Fixes issue with IntelliJ JUnit runner that stays yellow when executing JGiven tests (Issue [#58](https://github.com/TNG/JGiven/issues/58))

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
