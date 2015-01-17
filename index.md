---
layout: page
title: JGiven
---

## Behavior-Driven Development in Plain Java

JGiven is a developer-centric Behavior-Driven Development (BDD) tool for Java.

### Key facts

* Scenarios are written in plain Java with a _fluent_ Java API - Neither text files, nor Groovy is needed
* Method names are *parsed at runtime* and define the scenario text - No duplicate text in annotations is needed
* Scenarios are executed by either *JUnit* or *TestNG* - No extra test runner is needed, thus JGiven works with all existing IDEs and build tools for Java out-of-the-box
* Scenarios are composed of multiple, *reusable* so-called Stage classes - No more test code duplication
* Scenarios and steps can be parameterized for writing *data-driven* tests
* JGiven generates HTML reports that can be read and understand by domain experts and serve as a *living documentation*

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

Further examples can be found in the [jgiven-examples](https://github.com/TNG/JGiven/tree/master/jgiven-examples/src/test/java/com/tngtech/jgiven/examples) module of JGiven.


### HTML Report

Besides the plain text report, a HTML reports can be generated. Currently two different HTML reports can be generated one consisting of static HTML pages and a modern HTML5 report. An example of such a report is the [JGiven's own report]({{site.baseurl}}/jgiven-report/html5):
<p>
<a href="{{site.baseurl}}/jgiven-report/html5"><img id="jgivenreport" alt="JGiven HTML5 report of JGiven" src="img/html5report.png" /></a>
</p>
The source code of the corresponding JGiven tests are in the [jgiven-tests](https://github.com/TNG/JGiven/tree/master/jgiven-tests) module of the JGiven project.
