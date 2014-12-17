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
@DataProvider( { "1", "3", "10" } )
public void serving_a_coffee_reduces_the_number_of_available_coffees_by_one( int initialCoffees ) {
    given().a_coffee_machine().
        and().there_are_$_coffees_left_in_the_machine( initialCoffees ).

    when().I_insert_$_one_euro_coins( 2 ).
        and().I_press_the_coffee_button();

    then().a_coffee_should_be_served().
        and().there_are_$_coffees_left_in_the_machine( initialCoffees - 1 );
}
{% endhighlight %}

## Data Tables

Whenever the same scenario is executed multiple times with different parameters, JGiven generates a *data table*. For the above example JGiven will generate the following report:

<img alt="Data table example" src="{{site.baseurl}}/img/datatableexample.png" />

The interesting aspect here is that, although the test itself has only 1 parameter `initialCoffees`, the scenario itself is actually parameterized by 2 parameters `initialCoffees` and `coffeesLeft`. The second parameter is _derived_ from the first parameter. JGiven generates for all parameters that are not the same for all cases a column in the data table. If the parameter is derived, the name of the placeholder is the name of the parameter of the invoked step method.

Back: [Life-Cycle Methods]({{site.baseurl}}/docs/lifecycle/)