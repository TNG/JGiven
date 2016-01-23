[![Build Status](http://img.shields.io/travis/TNG/JGiven.svg?style=flat-square)](https://travis-ci.org/TNG/JGiven)
[![Coverage Status](http://img.shields.io/coveralls/TNG/JGiven.svg?style=flat-square)](https://coveralls.io/r/TNG/JGiven?branch=master)
[![Apache License 2.0](http://img.shields.io/badge/license-apache2-red.svg?style=flat-square)](http://opensource.org/licenses/Apache-2.0)
[![Maven Central](http://img.shields.io/maven-central/v/com.tngtech.jgiven/jgiven-junit.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.tngtech.jgiven/jgiven-junit)
[![Join the chat at https://gitter.im/TNG/JGiven](https://img.shields.io/gitter/room/nwjs/nw.js.svg?style=flat-square)](https://gitter.im/TNG/JGiven?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

<img src="http://jgiven.org/img/logo.png" height="80" alt="JGiven">

JGiven is a *developer-friendly* and *pragmatic* BDD tool for Java. Developers write scenarios in plain Java using a fluent, domain-specific API, JGiven generates reports that are readable by domain experts.

## Why another BDD tool?

Behavior-Driven Development (BDD) is a development method where business analysts, developers, and testers describe the behavior of a software product in a common language and notation. Behavior is typically described in terms of scenarios, which are written in the Given-When-Then notation. The common language and notation is one cornerstone of BDD. The other cornerstone is that the defined scenarios are executable, form a comprehensive test suite, and a living documentation for the software product.

In classical BDD tools for Java like [JBehave](http://jbehave.org) or [Cucumber](http://cukes.info) scenarios are written in plain text files. This allows non-developers to write scenarios, because no programming knowledge is required. To make scenarios executable, developers write so-called step-implementations. To bind plain text to step implementations regular expressions are used. For developers maintaining these executable scenarios has a high overhead that is not required if tests would be directly written in a programming language.

Beside the classical BDD tools there are a number of tools for Java to write BDD tests in a programming language like Groovy ([easyb](http://easyb.org)) or Scala ([ScalaTest](http://www.scalatest.org)). To our knowledge, however, there is no BDD tool where scenarios can be written in plain Java.

Finally, there are specification testing frameworks like [Spock](http://spock.org) (Groovy) or [LambdaBehave](http://richardwarburton.github.io/lambda-behave/) which are very developer-centric and good for unit-testing, but the generated reports are not in Given-When-Then form and are not easily readable by non-developers.

## BDD with JGiven

* Scenarios are written in standard Java code using a fluent, domain-specific API (no extra language like Scala or Groovy needed, no IDE plugin needed)
* Java method names and parameters are parsed during test execution (no extra annotations needed)
* Scenarios are executed by either JUnit or TestNG (no extra test runner needed)
* Scenarios consist of so-called stages, which share state by injection, providing a modular way of writing Scenarios.
* JGiven generates scenario reports for business owners and domain experts

## Example

```Java

@Test
public void a_pancake_can_be_fried_out_of_an_egg_milk_and_flour() {
    given().an_egg().
        and().some_milk().
        and().the_ingredient( "flour" );

    when().the_cook_mangles_everything_to_a_dough().
        and().the_cook_fries_the_dough_in_a_pan();

    then().the_resulting_meal_is_a_pancake();
}
```

The above test can be executed like any JUnit test.
During the execution, JSON files are generated that can then be used afterwards to generated test reports.
By default, a plain text report is shown in the console, which would look as follows:

```
Scenario: a pancake can be fried out of an egg milk and flour

  Given an egg
    And some milk
    And the ingredient flour
   When the cook mangles everything to a dough
    And the cook fries the dough in a pan
   Then the resulting meal is a pancake
```
In addition, you can generate a [HTML Report](http://jgiven.org/jgiven-report/html5/).

## Getting Started

1. Start by reading the [documentation](http://jgiven.org/docs/) section on JGiven's website.
2. See the [talk](http://www.techcast.com/events/bigtechday8/maffei-1345/) on JGiven held on the TNG Big TechDay

## Java 8 Note
If you are using Java 8 and JGiven version < 0.8.1, you should compile your test code with the `-parameters` option of the javac compiler. Otherwise JGiven cannot obtain the parameter names of step methods and will generate generic names of the form argX instead.

## License

JGiven is published under the Apache License 2.0, see
http://www.apache.org/licenses/LICENSE-2.0 for details.

## Contributing

See [CONTRIBUTING](CONTRIBUTING.md)

