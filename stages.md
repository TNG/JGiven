---
layout: page
title: Stages and State Injection
permalink: /docs/stages/
---

A JGiven scenario consists of multiple stages. Typically there is a stage for each phase of a scenario: a given stage, a when stage and a then stage, however, it is also possible to just use one stage or use arbitrary many stages. A stage is implemented by a stage class that contains methods representing the steps that can be used in the scenario. The big advantage of this modular concept is that stages can be easily reused by different scenarios.

## Step Methods

A stage class contains multiple _step methods_ that can be called in a scenario and that will appear in the report. Every *non-private*, *non-static* method of a stage class can be used as a step method. There are no further requirements. In particular, no annotations are needed. Step methods should return the `this` reference so that chaining of calls is possible. In addition, a step method should be written in `snake_case` so that JGiven knows the correct casing of each word of the step.

The following code shows a valid step method of the `WhenCook` stage:
{% highlight java %}
public WhenCook the_cook_mangles_everthing_to_a_dough() {
    return this;
}
{% endhighlight %}

JGiven removes the underlines so the step will appear as `the cook mangles everything to a dough` in the report.

### Overriding the Default Reporting
Sometimes it is necessary to override the default way of the step reporting. For example, if you want to use special characters that are not allowed in Java methods names, such as `(`, `+`, or `%`. You can then use the [`@As`]({{site.baseurl}}/javadoc/com/tngtech/jgiven/annotation/As.html) annotation to specify what JGiven should put into the report.

For example, if you define the following step method:
{% highlight java %}
@As("10% are added")
public WhenCalculator ten_percent_are_added() {
    return this;
}
{% endhighlight %}
The step will appear as `10% are added` in the report.

### Completely Hide Steps
Steps can be completely hidden from the report by using the [`@Hidden`]({{site.baseurl}}/javadoc/com/tngtech/jgiven/annotation/Hidden.html) annotation. This is sometimes useful if you need a technical method call within a scenario, which should not appear in the report.

For example:
{% highlight java %}
@Hidden
public void prepareRocketSimulator() {
    rocketSimulator = createRocketSimulator();
}
{% endhighlight %}

Note that it is useful to write hidden methods in `CamelCase` to make it immediately visible in the scenario that these methods will not appear in the report.

### Extended Descriptions
Steps can get an extended description with the [`@ExtendedDescription`]({{site.baseurl}}/javadoc/com/tngtech/jgiven/annotation/ExtendedDescription.html) annotation. You can use this to give additional information about the step to the reader of the report. In the HTML report this information is shown in a tooltip.

Example:
{% highlight java %}
@ExtendedDescription("Actually uses a rocket simulator")
public WhenRocketLauncher launch_rocket() {
    rocketSimulator.launchRocket();
    return this;
}
{% endhighlight %}

### Intro Words
If the predefined introductionary words in the [`Stage`]({{site.baseurl}}/javadoc/com/tngtech/jgiven/Stage.html) class are not enough for you and you want to define additional ones you can use the [`@IntroWord`]({{site.baseurl}}/javadoc/com/tngtech/jgiven/annotation/IntroWord.html) annotation on a step method.

Example:
{% highlight java %}
@IntroWord
public SELF however() {
   return self();
}
{% endhighlight %}

Note that you can combine `@IntroWord` with the `@As` annotation. To define a `,` as an introductionary word, for example, you can define:

{% highlight java %}
@IntroWord
@As(",")
public SELF comma() {
   return self();
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

   public SELF the_cook_mangles_everthing_to_a_dough() {
       meal = cook.makeADough( ingredients );
       return self();
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

## Subclassing of Stages

In practice, it often makes sense to have a hierarchy of stage classes. Your top stage class can implement common steps that you require very often, while subclasses implement more specialized steps.

One problem with subclassing stage classes is to keep the fluent interface intact. Let's have an example:

{% highlight java %}
public class GivenCommonSteps extend Stage<GivenCommonSteps> {
    public GivenCommonSteps my_common_step() {
        return this;
    }
}
{% endhighlight %}

Now assume that we create a subclass of `GivenCommonSteps`:

{% highlight java %}
public class GivenSpecialSteps extends GivenCommonSteps {
    public GivenSpecialSteps my_special_step() {
        return this;
    }
}
{% endhighlight %}

If you now want to use the `GivenSpecialSteps` stage, you will get problems when you want to chain multiple step methods:

{% highlight java %}
@Test
public void subclassing_of_stages_should_work() {
    given().my_common_step()
      .and().my_special_step();
}
{% endhighlight %}

This code will not compile, because `my_common_step()` returns `GivenCommonSteps` and not `GivenSpecialSteps`.

Luckily this problem can be fixed with generic types. First you have to change the `GivenCommonSteps` class as follows:

{% highlight java %}
public class GivenCommonSteps<SELF extends GivenCommonSteps<SELF>> extend Stage<SELF> {
    public SELF my_common_step() {
        return self();
    }
}
{% endhighlight %}
That is, you give `GivenCommonSteps` a type parameter `SELF` that can be specialized by subclasses. This type is also used as return type for `my_common_step()`. Instead of returning `this` you return `self()`, which is implemented in the `Stage` class.

Now your subclass must be change as well:

{% highlight java %}
public class GivenSpecialSteps<SELF extends GivenSpecialSteps<SELF>>
        extends GivenCommonSteps<SELF> {
    public SELF my_special_step() {
        return self();
    }
}
{% endhighlight %}


Back: [Report Generation]({{site.baseurl}}/docs/reportgeneration/) - Next: [Life-Cycle Methods]({{site.baseurl}}/docs/lifecycle/)
