---
layout: page
title: Stages and State Injection
permalink: /docs/stages/
---

A JGiven scenario consists of multiple stages. Typically there is a stage for each phase of a scenario: a given stage, a when stage and a then stage, however, it is also possible to just use one stage or use arbitrary many stages. A stage is implemented by a stage class that contains methods representing the steps that can be used in the scenario. The big advantage of this modular concept is that stages can be easily reused by different scenarios.

Stage classes are POJOs that follow the fluent-interface pattern. This means that all methods return the this-reference. In order to work together with inheritance, a stage class should have a type parameter SELF that extends the stage class itself. JGiven also provides the helper class Stage that provides a self() method to return the SELF type parameter. This is best understood by an example:

{% highlight java %}
public class GivenIngredients<SELF extends GivenIngredients> extends Stage<SELF> {
   List<String> ingredients = new ArrayList<String>();

   public SELF an_egg() {
      ingredients.add("Egg");
      return self();
   }
   ...
}
{% endhighlight %}

Stages share state by using injection. This works by annotating the fields with a special annotation @ScenarioState. The values of these fields are shared between all stages that have the same field.

For example, to be able to access the value of the ingredients field of the GivenIngredients stage in the WhenCook stage one has to annotate that field accordingly:

{% highlight java %}
public class GivenIngredients<SELF extends GivenIngredients> extends Stage<SELF> {
   @ScenarioState
   List<String> ingredients = new ArrayList<String>();
   ...
}
{% endhighlight %}

{% highlight java %}
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
{% endhighlight %}

Instead of the @ScenarioState annotation one can also use @ExpectedScenarioState and @ProvidedScenarioState to indicate whether the state is expected by the stage or provided by the stage.

Back: [Report Generation]({{site.baseurl}}/docs/reportgeneration/) - Next: [Parameters]({{site.baseurl}}/docs/parameters/)
