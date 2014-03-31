# JGiven v0.1

(c) 2013-2014 under the Apache License

## Back to the roots

* Write your scenarios in plain Java and use all your known IDE features without the need for any additional plugin
* No need for additional languages like Groovy or Scala
* No need to work with text files and regular expressions to bind text to code
* No need to write tests mixed with HTML
* No extra test-runner, just use JUnit or TestNG
* Get reports that business owners and domain experts can read and understand

### Example

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

## Principles

* Scenarios are written as Java code.
* No annotations are needed, because JGiven directly parses the Java method names and parameters during the test execution.
* Methods should be written in Snake_Case to have the correct case in the reports.
* Scenarios consist of so-called stages, which share state by injection

## Stages and State Injection

A JGiven scenario consists of multiple stages. Typically there is a stage for each phase of a scenario: a given stage, a when stage and a then stage,
however, it is also possible to just use one stage or use arbitrary many stages.
A stage is implemented by a stage class that contains methods representing the steps that can be used in the scenario.
The big advantage of this modular concept is that stages can be easily reused by different scenarios.

Stage classes are POJOs that follow the fluent-interface pattern. This means that all methods return the this-reference.
In order to work together with inheritance, a stage class should have a type parameter SELF that extends the stage class itself.
JGiven also provides the helper class Stage that provides a self() method to return the SELF type parameter.
This is best understood by an example:

```
public class GivenIngredients<SELF extends GivenIngredients> extends Stage<SELF> {
   List<String> ingredients = new ArrayList<String>();

   public SELF an_egg() {
      ingredients.add("Egg");
      return self();
   }
   ...
}
```

Stages share state by using injection. This works by annotating the fields with a special annotation @ScenarioState.
The values of these fields are shared between all stages that have the same field.

For example, to be able to access the value of the ingredients field of the GivenIngredients stage in the WhenCook stage one has to annotate that field accordingly:

```
public class GivenIngredients<SELF extends GivenIngredients> extends Stage<SELF> {
   @ScenarioState
   List<String> ingredients = new ArrayList<String>();
   ...
}
```

```
public class WhenCook<SELF extends WhenCook> extends Stage<SELF> {
   @ExpectedScenarioState
   List<String> ingredients = new ArrayList<String>();

   @ExpectedScenarioState
   String cook;

   @ProvidedScenarioState
   String meal;
   ...

   public WhenCook the_cook_mangles_everthing_to_a_dough() {
       meal = cook.makeADough( ingredients );
   }

}
```

Instead of the @ScenarioState annotation one can also use @ExpectedScenarioState and @ProvidedScenarioState to indicate whether the state is
expected by the stage or provided by the stage.

## Parameters

Method parameters are by default added to the end of the sentence as shown in the example above.

### Parameters within a sentence

Take the following example:
```
Given <neggs> eggs
```
This cannot be written directly as a single method name because parameters can only come at the end of the method.
JGiven solves this problem by using the $ character as follows:
```
given().$_eggs(neggs);
```
This is not perfectly readable at first cite, but as soon as you are used to reading $ as a parameter it comes quite natural.
The generated report will replace the $ with the corresponding parameter.
So the generated report will look as follows (given <neggs> was 5):
```
Given 5 eggs
```

# License

JGiven is published under the Apache License 2.0, see
http://www.apache.org/licenses/LICENSE-2.0 for details.

# Installation

## Maven

Note: JGiven is not yet published on Maven central, thus the following only works if JGiven is installed locally!

Add the following dependency to your POM file:

```
<dependency>
   <groupId>com.tngtech</groupId>
   <artifactId>jgiven-junit</artifactId>
   <version>0.1</version>
   <scope>test</scope>
</dependency>
```

for TestNG instead of JUnit use:
```
<dependency>
   <groupId>com.tngtech</groupId>
   <artifactId>jgiven-testng</artifactId>
   <version>0.1</version>
   <scope>test</scope>
</dependency>
```
