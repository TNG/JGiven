# v1.0.0

This is the first major version update of JGiven. It does not introduce major new features, but it introduces backwards-incompatible changes
due to the removal of all deprecated classes and methods and the drop of Java 7 support.

## New Features

* Java 13 is supported now [#447](https://github.com/TNG/JGiven/pull/447) (thanks to jsalinaspolo)
* The `CurrentStep` interface has a new method `setName` to change the name of a step programmatically [#386](https://github.com/TNG/JGiven/issues/386)
* Updated the Guava dependency to v27.1-jre [#398](https://github.com/TNG/JGiven/pull/398)
* Added `DualScenarioTest` [#406](https://github.com/TNG/JGiven/pull/406) (thanks to jangalinksi)
* Upgraded Gradle Plugin to support Gradle 5 and 6 [#381](https://github.com/TNG/JGiven/pull/381) (thanks to jsalinaspolo)
* `@Pending` can now be added to the test class to make all scenarios of that class pending [#403](https://github.com/TNG/JGiven/issues/403)
* Added new option 'jgiven.report.dry-run' to generate a report without really executing the tests [#435](https://github.com/TNG/JGiven/issues/435) (thanks to jsalinaspolo)
* Added Portuguese scenario and stage classes [#423](https://github.com/TNG/JGiven/issues/423) (thanks to gandadil)

### Spring 5 with JUnit 5

To better support Spring 5 with JUnit 5 the `jgiven-spring` module has been split up into three modules.
You need to adapt your dependencies accordingly. If you are using JUnit 4 use the `jgiven-spring-junit4` module.
If you are using Spring 5 with JUnit 5 then use the `jgiven-spring-junit5` module.

## Backwards Incompatible Changes

* Java 7 is not supported anymore
* Calling stage methods annotated with `@DoNotIntercept` or declared within `java.lang.Object` will not trigger
a stage change anymore [#385](https://github.com/TNG/JGiven/pull/385)
* The Guava dependency changed to v27.1-jre. This might lead to problems in case your project also depends on Guava

## Cleaned up Module Dependencies

Some direct module dependencies have been removed. This means that you might have to add additional
dependencies to your build:

* jgiven-junit has no direct junit dependency anymore and no direct dependency to jgiven-html5-report

## Removed Deprecated Features

All deprecated methods and classes have been removed. Please adapt your code according to the JavaDoc documentation.

* Removed `@NotImplementedYet` annotation. Use `@Pending` instead.
* Removed `@CaseDescription` annotation. Use `@CaseAs` instead.
* Removed `CaseDescriptionProvider`. Use `CaseAsProvider` instead.
* Removed `DefaultCaseDescriptionProvider`. Use `DefaultCaseAsProvider` instead.
* Removed `com.tngtech.jgiven.junit.de.SzenarioTest`. Use `com.tngtech.jgiven.junit.lang.de.SzenarioTest` instead.
* Removed `ScenarioExecutionRule`. Use `JGivenMethodRule` instead.
* Removed `ScenarioReportRule`. Use `JGivenClassRule` instead.
* Removed `SpringCanWire`

## Fixed Issues

* Make it easier to copy the stack trace from an error message [#380](https://github.com/TNG/JGiven/issues/380)
* `TestEntitiyManager` now initialized correctly [#415](https://github.com/TNG/JGiven/issues/415) (thanks to leimer)
* make tasks in the JGiven-gradle plugin cacheable [#450](https://github.com/TNG/JGiven/pull/450) (thanks to jsalinaspolo)

# v0.18.2

## Fixed Issues

* Fixed the `setName` method introduced in v0.18.0 to correctly update the words in the report [#386](https://github.com/TNG/JGiven/issues/386)

# v0.18.1

## Exception handling changed for TestNG

When using TestNG, exceptions are not longer caught and suppressed until the end of a scenario.
This means that steps following a failed step are no longer shown in the scenario report.
This change was needed to ensure that TestNG reports the correct test status in case of an exception.

See [PR #422](https://github.com/TNG/JGiven/pull/422) and [Issue #312](https://github.com/TNG/JGiven/issues/312) for details.

## Fixed Issues

* Fix issue with @Pending(executeSteps = true) that marked a scenario as failed instead of pending [#402](https://github.com/TNG/JGiven/issues/402)
* Make TestNG ScenarioTestListener work with parallel="tests" option [#409](https://github.com/TNG/JGiven/issues/409)

# v0.18.0

## New Features

* The `CurrentStep` interface has a new method `setName` to change the name of a step programmatically [#386](https://github.com/TNG/JGiven/issues/386)

## Fixed Issues

* Fixed calculation of minimal thumbnail sizes [#417](https://github.com/TNG/JGiven/issues/417)

# v0.17.1

## Fixed Issues

* Fix NPE when having @Disabled test in JUnit 5 [#376](https://github.com/TNG/JGiven/issues/376)

# v0.17.0

## New Features

* Java 11 is supported now [#370](https://github.com/TNG/JGiven/issues/370)

## Fixed Issues

* Fixed an issue with JUnit 5 and parameterized tests to get the argument values [#372](https://github.com/TNG/JGiven/pull/372)

# v0.16.1

## New Features

* First release of the Spock integration [#358](https://github.com/TNG/JGiven/pull/358) (thanks to mustaine)
* TestNG SkipExceptions are now recognized [#355](https://github.com/TNG/JGiven/issues/355)

## Fixed Issues

* Fixed a bug that prevented the use of the PowerMockRunner [#365](https://github.com/TNG/JGiven/issues/365)
* Fixed a NPE that was thrown when the `@Table` annotations was used with an empty list [#341](https://github.com/TNG/JGiven/issues/341)

# v0.16.0

## New Features

* Java 10 is supported now [#345](https://github.com/TNG/JGiven/issues/345)

## Fixed Issues

* Fixed an IllegalArgumentException when creating thumbnails for very small images [#329](https://github.com/TNG/JGiven/issues/329) (thanks to maccluca)
* Fixed NPE when using JUnit 5 and one test class has only disabled method [#338](https://github.com/TNG/JGiven/issues/338)
* Fixed minor issue in JUnit 5 example that did not generate the HTML 5 report [#340](https://github.com/TNG/JGiven/issues/340)

## Backwards Incompatible Changes

* Java 6 is not supported anymore

# v0.15.3

## Fixed Issues

* Removed usages of `javax.xml.bind`, so that JGiven can be used with Java 9 without hassle [#324](https://github.com/TNG/JGiven/issues/324)

# v0.15.2

## Fixed Issues

* Fixed a `NullPointerException` in the PojoFormatter when passed object is `null` [#318](https://github.com/TNG/JGiven/issues/318)
* Fixed JUnit 5.0.0 compilation issue due to API changes [#326](https://github.com/TNG/JGiven/issues/326)

## JGiven HTML App

The JGiven HTML5 App is extracted into its own [project](https://github.com/jgiven/jgiven-html-app).
This allows us to develop the HTML5 App independently of JGiven as it is also used by [jsGiven](https://github.com/jsGiven/jsGiven).
However, the App will also be delivered together with JGiven so for users of JGiven nothing should change.

# v0.15.1

## Fixed Issues

* Fixed a `NullPointerException` when using `@Table(includeNullColumns = true)` and columns with null values [#315](https://github.com/TNG/JGiven/issues/315)

# v0.15.0

## New Features

* Thumbnail preview for image attachments added [#299](https://github.com/TNG/JGiven/pull/299)
* The ReportGenerator now uses the HTML5 report as default, doesn't silently misinterprets wrong arguments and flags and offers suggestions  [#299](https://github.com/TNG/JGiven/pull/299)
* Formatting POJOs has been greatly improved, by allowing to specify custom formatters for fields [#297](https://github.com/TNG/JGiven/pull/297) (thanks to dgrandemange)
* @ExtendedDescription supports parameter place holders now [#283](https://github.com/TNG/JGiven/pull/283)
* The HTML App has been extracted into a separate project and has been refactored internally. The functionality should not have been changed. [#287](https://github.com/TNG/JGiven/pull/287)

## Fixed Issues
* Upgraded to ByteBuddy 1.6.x to fix backwards-incompatibility issues when JGiven is used with Mockito 2.7.19 [#309](https://github.com/TNG/JGiven/issues/309)
* Introduced CaseAs annotation to replace the CaseDescription annotation [#301](https://github.com/TNG/JGiven/pull/301)
* Removed ambiguity between parsing of As, CaseAs and ExtendedDescription. Argument enumeration starts from 1, internal count of how often placeholders are used, see docs to As for every feature. All argument reference types are interoperable [#301](https://github.com/TNG/JGiven/pull/301)


# v0.14.1

## Fixed Issues

* Performance: Caching the classes generated with ByteBuddy introduced with v0.14.0 to decrease the memory consumption and performance of creating stage classes [#294](https://github.com/TNG/JGiven/issues/294)
* OSGi: Using class loader of the stage class instead of the current thread when creating classes with ByteBuddy [#302](https://github.com/TNG/JGiven/issues/302)
* HTML Report: Long exception messages of failed Scenarios are now wrapped [#292](https://github.com/TNG/JGiven/issues/292)

# v0.14.0

## Switch from cglib to ByteBuddy

The internal JGiven interception mechanism was changed to use ByteBuddy instead of cglib. The main reason for this change is support for Android (see below).
From a users perspective, JGiven should behave as before.

## Backwards Incompatible Changes

### Spring Integration

In order to fix issue #259 the Spring integration was largely rewritten. If you only had used the `@EnableJGiven` and `@JGivenStage` annotations, nothing should change.
If you had a custom Spring configuration for JGiven you have to change the following:

* The classes `SpringStepMethodInterceptor` and `JGivenStageAutoProxyCreator` do not exist anymore, you have to remove all references
* As a replacement the new class `JGivenBeanFactoryPostProcessor` exists now. You have to register this bean in your Spring configuration

## New Features

* Experimental support for JUnit 5 has been added [#277](https://github.com/TNG/JGiven/pull/277)
* Tags with the same name, but different packages are now correctly distinguished [#242](https://github.com/TNG/JGiven/pull/242) (thanks to ahus1)
* Scenario states can be marked as required to make scenarios fail quickly and with a clear message if the state hasn't been provided [#255](https://github.com/TNG/JGiven/issues/255) (thanks to Airblader)
* Added prefined Spanish JGiven classes for writing JGiven Scenarios in Spanish [#284](https://github.com/TNG/JGiven/pull/284) (thanks to elenagarcia5)

### Experimental JUnit 5 Support

JGiven can now be used together with JUnit 5, by using the `JGivenExtension`. Refer to the user guide for additional details. [#277](https://github.com/TNG/JGiven/pull/277)

### Experimental Android Support

There is a new experimental module called `jgiven-android`, which enables JGiven support for tests executed on the device or simulator.
This makes it possible to combine JGiven with Espresso tests and even integrate screenshots in the JGiven report.
For details of how to setup and use the Android support have a look at the `jgiven-android-test` project.

Also see [#258](https://github.com/TNG/JGiven/pull/258)

Special thanks to [orginx](https://github.com/originx)

## Fixed Issues

* Spring Integration: Nested Steps are now supported when using Spring [#259](https://github.com/TNG/JGiven/issues/259)

# v0.13.1

## Bug Fixes

* Maven Plugin: The default values of the Maven plugin now work again [#276](https://github.com/TNG/JGiven/issues/276)

# v0.13.0

## Backwards Incompatible Changes

In order to fix issue #239, a backwards incompatible change had to be done:

* The `ScenarioBase` class is now abstract, because the method `getScenario()` was made abstract. Thus, subclasses have to implement the `getScenario()` method.
  As, in general, you should have inherited either from `ScenarioTest` or `SimpleScenarioTest` the change should most likely not effect you.
  If you have directly inherited from `ScenarioBase`, have a look at the `ScenarioTest` class of how to implement the `getScenario()` method.

## New Features

* Custom annotations can now also be defined for the `@Table` annotation [#235](https://github.com/TNG/JGiven/issues/235)
* In addition to text and images, all kinds of media types can now be used as attachments [#228](https://github.com/TNG/JGiven/issues/228) (thanks to ahus1)

## Small Improvements

* Assertion errors shown in the HTML report respect line breaks now. [#234](https://github.com/TNG/JGiven/issues/234)

## Bug Fixes

* TestNG: executing test methods in parallel is now possible. [#239](https://github.com/TNG/JGiven/issues/239)
* Correctly handle nested steps in parametrized scenarios. [#248](https://github.com/TNG/JGiven/issues/248)
* Correctly report pending status of parametrized scenarios. [#200](https://github.com/TNG/JGiven/issues/200)
* Spring: added support for executing Spring tests with the Spring JUnit rules instead of the Spring test runner. [#250](https://github.com/TNG/JGiven/pull/250)


# v0.12.1

## Fixed Issue

* Fixed an issue in the HTML5 report to correctly show the value instead of an unresolved funtion [#233](https://github.com/TNG/JGiven/issues/233)

# v0.12.0

## New Features

* Added possibility to use JGiven in JUnit by just using two rules. No deriving from ScenarioTest is necessary anymore, see [#232](https://github.com/TNG/JGiven/pull/232)
* Allow multiple formatter annotations on arguments, e.g., "@Quoted @YesNo", see [#204](https://github.com/TNG/JGiven/issues/204).
* Added a new comment() method to provide further information on specific step method invocations, see [#50](https://github.com/TNG/JGiven/issues/50).
* Steps can now have multiple attachments [#194](https://github.com/TNG/JGiven/issues/194).
* Tags can now be hidden from the navigation bar in the HTML report by setting the `showInNavigation` attribute to `false` [#211](https://github.com/TNG/JGiven/issues/211).
* Added a new CurrentScenario interface similar to CurrentStep.
* The CurrentScenario interface allows adding tags programmatically, see [#172](https://github.com/TNG/JGiven/issues/172).
* Allow tag annotations on step methods and step classes.
* Extended the @As annotation with a provider mechanism, see [#189](https://github.com/TNG/JGiven/issues/189).

## Breaking Changes in the JSON model

* Due to the introduction of multiple attachments per step, the JSON model had to be changed in an backwards-incompatible way. Instead of a single field `attachment` that holds a single attachment object, a step has now an `attachments` field that holds an array of attachment objects.

## Fixed Issues

* Fixed an issue that step methods that are all uppercase are formatted in an unexpected way [#221](https://github.com/TNG/JGiven/issues/221)
* Fixed an issue that newlines of formatted arguments have not been formatted in the HTML5 report [#226](https://github.com/TNG/JGiven/issues/226)

# v0.11.4

## Fixed Issues

* Don't show multiple indexed attachments when cases are not shown as tables [#207](https://github.com/TNG/JGiven/pull/207) (thanks to ahus1)

# v0.11.3

## New Features

* The @CaseDescription annotation can now also be applied to the test class [#198](https://github.com/TNG/JGiven/issues/198)

## Fixed Issues

* Fixed an issue introduced with v0.11.1 that made the report generation dependent on the locale of the system instead of using utf-8 [PR#196](https://github.com/TNG/JGiven/pull/196) (thanks to ahus1)

# v0.11.2

## Fixed Issues

* Fixed an issue introduced with v0.11.1 that for scenarios with attachments and multiple cases, only the attachment of the first case was shown in the HTML report [#191](https://github.com/TNG/JGiven/issues/191)

# v0.11.1

## New Features

### HTML Report size reduction

* The size of the HTML report has been greatly reduced by compressing the scenario data with gzip. This significantly reduces the load time of large reports on slow network connections. [#186](https://github.com/TNG/JGiven/issues/186)

## Fixed Issues

* HTML Report: fixed an issue with the search input in the mobile menu that was hidden on mobile devices when the virtual keyboard appeared. [#182](https://github.com/TNG/JGiven/issues/182)
* JUnit: throwing an AssumptionViolationException will not lead to a failed scenario anymore. Instead, the scenario will be ignored and will not appear in the report at all. [#185](https://github.com/TNG/JGiven/issues/185)

# v0.11.0

## New Features

### Sections

Scenarios can have sections now. This allows you to structure larger scenarios into several parts with a title. [PR#181](https://github.com/TNG/JGiven/pull/181)

Example:
```
section("This is a section title");
given().something();
when().something();

section("This is another section title");
when().something_else();
then().something();
```

Sections appear in the console output as well as the HTML report.

### Changes regarding colors in the console output

The color used in the console output has been changed. It is now less colorful and usable on dark and light backgrounds. Setting the system property `jgiven.report.text.color` to `true` now always enables the color output, even if the output is not a TTY.

## Fixed Issues

* Fixed an issue with carriage returns messing up the cases table. [#178](https://github.com/TNG/JGiven/issues/178)
* Fixed an issue with certain characters used in a `@CaseDescription` annotation. [#177](https://github.com/TNG/JGiven/issues/177)

# v0.10.1

## New Features

### Nested Steps

Steps can now have nested steps that are shown in the report. This is done by annotating parent steps with the new `@NestedSteps` annotation. [#17](https://github.com/TNG/JGiven/issues/17), [PR#174](https://github.com/TNG/JGiven/pull/174). Thanks to @albertofaci!

### Additional Table Formatting Options

The `@Table` annotation to format step parameters as tables has been extended with several options to further customize the formatting of the resulting tables. [#122](https://github.com/TNG/JGiven/issues/122)

### Other Features

* HTML Report: case tables have sortable columns [#175](https://github.com/TNG/JGiven/pull/175)
* HTML Report: case tables can be grouped by values [#168](https://github.com/TNG/JGiven/issues/168)
* Exception type is now added to the error message of a failed step [#154](https://github.com/TNG/JGiven/issues/154)

## Fixed Issues

* Fixed the issue that tags of subclasses would not be visible on scenarios of superclasses [#171](https://github.com/TNG/JGiven/issues/171)
* Fixed the issue that exceptions thrown in methods called within step methods are captured [#173](https://github.com/TNG/JGiven/issues/173)

# v0.10.0

* This version was published in a broken state to maven central, please use version v0.10.1 instead

# v0.9.5

## Fixed Issues

* Fixed issue with primitive arrays and the @Table annotation [#162](https://github.com/TNG/JGiven/issues/162)
* Fixed an issue when using the `@Table` parameter that could lead to unwanted parameters in the data table [#161](https://github.com/TNG/JGiven/issues/161)
* Fixed an issue with the Maven Plugin where the customJsFile parameter actually set the customCssFile parameter [#167](https://github.com/TNG/JGiven/issues/167)
* Fixed an issue with the @Table annotation when combining numberedRows with columnTitles [#166](https://github.com/TNG/JGiven/issues/166)
* Fixed an issue in the console report that new lines a data table messed up the layout of the table [#152](https://github.com/TNG/JGiven/issues/152)

## New Features

* CamelCase in step methods is now supported (thanks to albertofaci) [#164](https://github.com/TNG/JGiven/issues/164), [PR #165](https://github.com/TNG/JGiven/pull/165)

# v0.9.4

## Fixed Issues

* Fixed an issue that blank values could not be formatted [#157](https://github.com/TNG/JGiven/issues/157)
* Fixed an issue that the total duration of a scenario was shown as the duration of the first case instead of the sum of all case [#155](https://github.com/TNG/JGiven/issues/155)
* HTML Report: fixed the minor issue that clicking the status doughnot does not work when the report has just been opened [#149](https://github.com/TNG/JGiven/issues/149)

## New Features

* Spring support has been simplified by a new annotation `@EnableJGiven` and XML element <jgiven:annotation-driven />. Special thanks to TripleNail for this contribution. [#153](https://github.com/TNG/JGiven/pull/153)
* Scenarios without steps can now be excluded from the report by using the new `--exclude-empty-scenarios` report generator option [#151](https://github.com/TNG/JGiven/issues/151)
* Underlines in parameter names are now replaced with spaces in the report [#147](https://github.com/TNG/JGiven/issues/147)
* HTML Report: highlight failing cases of a scenario [#150](https://github.com/TNG/JGiven/issues/150)
* HTML Report: indicate the number of failed cases [#156](https://github.com/TNG/JGiven/issues/156)
* HTML Report: limit the width of columns in the data table [#141](https://github.com/TNG/JGiven/issues/141)

# v0.9.3

## Fixed Issues

* HTML Report: fixed an issue that inline attachments are shown multiple times [#145](https://github.com/TNG/JGiven/issues/145)
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
