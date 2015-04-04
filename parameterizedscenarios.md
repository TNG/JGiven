---
layout: page
title: Parameterized Scenarios
permalink: /docs/parameterizedscenarios/
---

JGiven scenarios can be parameterized. This is very useful for writing data-driven scenarios, where the scenarios itself are the same, but are executed with different example values.

Parameterization of Scenarios works with TestNG and JUnit, we only show it for JUnit. For TestNG it works analogous.

## JUnit

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
    "1, 0",
} )
public void coffee_is_not_served( int coffees, int dollars) {
    given().there_are_$_coffees_left_in_the_machine( coffees ).
        and().the_coffee_costs_$_dollars( 2 );

    when().I_deposit_$_dollars( dollars ).
        and().I_press_the_coffee_button();

    then().I_should_not_be_served_a_coffee();
}
{% endhighlight %}

The resulting report will then look as follows:

```
Scenario: coffee is not served

  Given there are <coffees> coffees left in the machine
    And the coffee costs 2 dollars
   When I deposit <dollars> dollars
    And I press the coffee button
   Then I should not be served a coffee

 Cases:

   | # | <coffees> | <dollars> | Status  |
   +---+-----------+-----------+---------+
   | 1 |         1 |         1 | Success |
   | 2 |         0 |         2 | Success |
   | 3 |         1 |         0 | Success |
```

Back: [Parameterized Steps]({{site.baseurl}}/docs/parameterizedsteps/) - Next: [Tags]({{site.baseurl}}/docs/tags/)
