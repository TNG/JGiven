---
layout: page
title: Getting Started
permalink: /docs/gettingstarted/
---

JGiven can be used together with JUnit or TestNG, here we assume you are using JUnit.

## Create a JUnit test class

First of all you create a JUnit test class that inherits from `com.tngtech.jgiven.junit.ScenarioTest`:

{% highlight java %}
import com.tngtech.jgiven.junit.ScenarioTest;

public class MyShinyJGivenTest extends
   ScenarioTest<GivenSomeState<?>, WhenSomeAction<?>, ThenSomeOutcome<?>> {

}
{% endhighlight %}

The `ScenarioTest` requires 3 type parameters. Each of these type parameters represents a stage of the Given-When-Then notation. Note that there is also the `SimpleScenarioTest` class that only requires a single type parameter. In that case, all your scenario steps are defined in a single class.

## Create Given, When, and Then classes

To make your class compile, create the following three classes:

{% highlight java %}
import com.tngtech.jgiven.Stage;

public class GivenSomeStage<SELF extends GivenSomeStage<?>> extends Stage<SELF> {
   public SELF some_state() {
      return self();
   }
}

public class WhenSomeAction<SELF extends WhenSomeAction<?>> extends Stage<SELF> {
   public SELF some_action() {
      return self();
   }
}

public class ThenSomeOutcome<SELF extends ThenSomeOutcome<?>> extends Stage<SELF> {
   public SELF some_outcome() {
      return self();
   }
}
{% endhighlight %}

JGiven does not require to inherit from the `Stage` class, however, the `Stage` class already provides some useful methods like `and()` and `self()`. Also note that the `SELF` type parameter is not required, however, it is very useful if you plan to subclass stages.

## Write your first scenario

Now you can write your first scenario

{% highlight java %}
import org.junit.Test;
import com.tngtech.jgiven.junit.ScenarioTest;

public class MyShinyJGivenTest extends
   ScenarioTest<GivenSomeState<?>, WhenSomeAction<?>, ThenSomeOutcome<?>> {

   @Test
   public void something_should_happen() {
      given().some_state();
      when().some_action();
      then().some_outcome();
   }
}
{% endhighlight %}

## Execute your scenario

The scenario is execute like any other JUnit test, for example, by using your IDE or Maven:
{% highlight bash %}
$ mvn test
{% endhighlight %}

Next: [Report Generation]({{site.baseurl}}/docs/reportgeneration/)
