---
layout: page
title: Stages and State Injection
permalink: /docs/stages/
---

A JGiven scenario consists of multiple stages. Typically there is a stage for each phase of a scenario: a given stage, a when stage and a then stage, however, it is also possible to just use one stage or use arbitrary many stages. A stage is implemented by a stage class that contains methods representing the steps that can be used in the scenario. The big advantage of this modular concept is that stages can be easily reused by different scenarios.

## Fluent Interface Pattern

Stage classes are POJOs that follow the fluent-interface pattern. This means that all methods return the this-reference. In order to work together with inheritance, a stage class should have a type parameter SELF that extends the stage class itself. JGiven also provides the helper class Stage that provides a `self()` method to return the SELF type parameter. This is best understood by an example:

{% highlight java %}
public class GivenIngredients<SELF extends GivenIngredients<?>> extends Stage<SELF> {
   List<String> ingredients = new ArrayList<String>();

   public SELF an_egg() {
      ingredients.add("Egg");
      return self();
   }
   ...
}
{% endhighlight %}

## State Injection

Stages share state by using injection. This works by annotating the fields with a special annotation `@ScenarioState`. The values of these fields are shared between all stages that have the same field.

For example, to be able to access the value of the ingredients field of the GivenIngredients stage in the `WhenCook` stage one has to annotate that field accordingly:

{% highlight java %}
public class GivenIngredients<SELF extends GivenIngredients<?>> extends Stage<SELF> {
   @ScenarioState
   List<String> ingredients = new ArrayList<String>();
   ...
}
{% endhighlight %}

{% highlight java %}
public class WhenCook<SELF extends WhenCook<?>> extends Stage<SELF> {
   @ExpectedScenarioState
   List<String> ingredients;

   @ExpectedScenarioState
   String cook;

   @ProvidedScenarioState
   String meal;
   ...

   public WhenCook the_cook_mangles_everthing_to_a_dough() {
       meal = cook.makeADough( ingredients );
   }

}
{% endhighlight %}

Instead of the `@ScenarioState` annotation one can also use `@ExpectedScenarioState` and `@ProvidedScenarioState` to indicate whether the state is expected by the stage or provided by the stage.

## Type vs. Name Resolution
Scenario state fields are by default resolved by its type. That is, you can only have one field of the same type as a scenario field. Exceptions are types from the packages `java.lang.*` and `java.util.*` which are resolved by the name of the field.

### Change The Resolution Strategy
To change the resolution strategy you can use the `resolution` parameter of the `@ScenarioState` annotations. For example, to use name instead of type resolution you can write

`@ScenarioState(resolution = Resolution.NAME)`.

## Having More Than 3 Stages
In many cases three stages are typically enough to write a scenario. However, sometimes more than three are required. JGiven provides two mechanism for that: stage injection and dynamic adding of stages.

### Stage Injection
Additional stages can be injected into a test class by declaring a field with the additional stage and annotate it with `@ScenarioStage`.

#### Example
In the following example we inject an additional stage `GivenAdditionalState` into the test class and use it in the test.

{% highlight java %}
import org.junit.Test;
import com.tngtech.jgiven.junit.ScenarioTest;

public class MyShinyJGivenTest extends
   ScenarioTest<GivenSomeState<?>, WhenSomeAction<?>, ThenSomeOutcome<?>> {

   @ScenarioStage
   GivenAdditionalState<?> additionalState;

   @Test
   public void something_should_happen() {
      given().some_state();

      additionalState
         .and().some_additional_state();

      when().some_action();
      then().some_outcome();
   }
}
{% endhighlight java %}

Note that the field access will not be visible in the report. Thus the resulting report will look as follows:

```
Scenario: something should happen

  Given some state
    And some additional state
   When some action
   Then some outcome
```


Also note that you should not forget to first invoke an intro method, like `and()` or `given()` on the injected stage before calling the step method.

### Dynamic Adding of Stages
The disadvantage of injecting a stage into a test class is that this stage will be used for *all* tests of that class. This might result in an overhead if the stage contains `@BeforeScenario` or `@AfterScenario` methods, because these methods will also be executed in the injected stages. If an additional stage is only required for a single test method you should instead dynamically add that stage to the scenario by using the `addStage` method.

#### Example
{% highlight java %}
import org.junit.Test;
import com.tngtech.jgiven.junit.ScenarioTest;

public class MyShinyJGivenTest extends
   ScenarioTest<GivenSomeState<?>, WhenSomeAction<?>, ThenSomeOutcome<?>> {

   @Test
   public void something_should_happen() {
      GivenAdditionalState<?> additionalState = addStage(GivenAdditionalState.class);

      given().some_state();

      additionalState
         .and().some_additional_state();

      when().some_action();
      then().some_outcome();
   }
}
{% endhighlight java %}

Back: [Report Generation]({{site.baseurl}}/docs/reportgeneration/) - Next: [Life-Cycle Methods]({{site.baseurl}}/docs/lifecycle/)
