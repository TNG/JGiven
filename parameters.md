---
layout: page
title: Parameters
permalink: /docs/parameters/
---

Method parameters are by default added to the end of the sentence as shown in the example above.

## Parameters within a sentence

Take the following example:
{% highlight java %}
Given <neggs> eggs
{% endhighlight %}
This cannot be written directly as a single method name because parameters can only come at the end of the method.
JGiven solves this problem by using the $ character as follows:
{% highlight java %}
given().$_eggs(neggs);
{% endhighlight %}
This is not perfectly readable at first glance, but as soon as you are used to reading $ as a parameter it comes quite natural.
The generated report will replace the $ with the corresponding parameter.
So the generated report will look as follows (given neggs is 5):

{% highlight java %}
Given 5 eggs
{% endhighlight %}

## Parameterized JUnit Tests

JGiven supports several different ways to parameterize a JUnit test:

1. JUnit's built-in Parametrized Runner
1. [JUnit-Dataprovider](https://github.com/TNG/junit-dataprovider)
1. [JUnitParms](https://code.google.com/p/junitparams/)

### JUnit-Dataprovider Runner

[JUnit-Dataprovider](https://github.com/TNG/junit-dataprovider) provides a JUnit test runner that enables the execution of paramterized test methods.
It is similar to the way parameterized tests work in [TestNG](http://testng.org).

#### Example

{% highlight java %}
@Test
@DataProvider( {
    "1, 1",
    "0, 2",
    "1, 2",
} )
public void coffee_is_not_served( int coffees, int dollars) {
    given().there_are_$_coffees_left_in_the_machine( coffees ).
        and().the_coffee_costs_$_dollar( 2 );

    when().I_deposit_$_dollar( dollars ).
        and().I_press_the_coffee_button();

    then().I_should_not_be_served_a_coffee();
}
{% endhighlight %}

Back: [Life-Cycle Methods]({{site.baseurl}}/docs/lifecycle/) - Next: [Tags]({{site.baseurl}}/docs/tags/)