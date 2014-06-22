---
layout: page
title: JGiven
---

## Pragmatic BDD for Java

* Write Given-When-Then scenarios in plain Java
* Use all IDE features without the need for any additional plugin
* No need for additional languages like Groovy or Scala
* No need to work with text files and regular expressions to bind text to code
* No need to write tests mixed with HTML
* No extra test-runner, just use JUnit or TestNG
* Get reports that business owners and domain experts can read and understand

### Example

{% highlight java %}
@Test
public void a_pancake_can_be_fried_out_of_an_egg_milk_and_flour() {
    given().an_egg().
        and().some_milk().
        and().the_ingredient( "flour" );

    when().the_cook_mangles_everthing_to_a_dough().
        and().the_cook_fries_the_dough_in_a_pan();

    then().the_resulting_meal_is_a_pan_cake();
}
{% endhighlight %}

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
