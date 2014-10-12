# JGiven
[![Build Status](http://img.shields.io/travis/TNG/JGiven.svg?style=flat-square)](https://travis-ci.org/TNG/JGiven)
[![Coverage Status](http://img.shields.io/coveralls/TNG/JGiven.svg?style=flat-square)](https://coveralls.io/r/TNG/JGiven?branch=master)
[![Apache License 2.0](http://img.shields.io/badge/license-apache2-red.svg?style=flat-square)](http://opensource.org/licenses/Apache-2.0)
[![Maven Central](http://img.shields.io/maven-central/v/com.tngtech.jgiven/jgiven-junit.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.tngtech.jgiven/jgiven-junit)



JGiven is a pragmatic BDD tool for Java.

## Why another BDD tool?

Behavior-Driven Development (BDD) is a development method where business analysists, developers, and testers describe the behavior of a software product in a common language and notation. Behavior is typically described in terms of scenarios, which are written in the Given-When-Then notation. The common language and notation is one cornerstone of BDD. The other cornerstone is that the defined scenarios are executable, form a comprehensive test suite, and a living documentation for the software product.

In classical BDD tools for Java like [JBehave](http://jbehave.org) or [Cucumber](http://cukes.info) scenarios are written in plain text files. This allows non-developers to write scenarios, because no programming knowledge is required. To make scenarios executable, developers write so-called step-implementations. To bind plain text to step implementations regular expressions are used. For developers maintaining these executable scenarios has a high overhead that is not required if tests would be directly written in a programming language.

Beside the classical BDD tools there are a number of tools for Java to write BDD tests in a programming language like Groovy ([easyb](http://easyb.org)) or Scala ([ScalaTest](http://www.scalatest.org)). To our knowledge, however, there is no BDD tool where scenarios can be written in plain Java.

Finally, there are specification testing frameworks like [Spock](http://spock.org) (Groovy) or [LambdaBehave](http://richardwarburton.github.io/lambda-behave/) which are very developer-centric and good for unit-testing, but the generated reports are not in Given-When-Then form and are not easily readable by non-developers.

## BDD with JGiven

* Scenarios are written as standard Java code (no extra language like Scala or Groovy needed, no IDE plugin needed)
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

    when().the_cook_mangles_everthing_to_a_dough().
        and().the_cook_fries_the_dough_in_a_pan();

    then().the_resulting_meal_is_a_pan_cake();
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

   Then the resulting meal is a pan cake
```

## Getting Started

Start by reading the [documentation](http://jgiven.org/docs/) section on JGiven's website.

## License

JGiven is published under the Apache License 2.0, see
http://www.apache.org/licenses/LICENSE-2.0 for details.


## Contributing

1. Fork the project
2. Create a new feature branch with your contribution
3. Implement your great new feature or bug fix
4. Issue a pull request

### Code Format

Please follow the code format of existing code.
You can ensure this by using the provided Eclipse formatter `develop/eclipse-formatter.xml`.
