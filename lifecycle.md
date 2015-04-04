---
layout: page
title: Life-Cycle Methods
permalink: /docs/lifecycle/
---

## Scenario Life-Cycle

The life-cycle of a scenario is as follows.

1. An instance for each stage class is created
2. The `before()` methods of all scenario rules of all stages are called
3. The `@BeforeScenario`-annotated methods of all stages are called
4. For each stage:
4.1. Values are injected into all scenario state fields
4.2. The `@BeforeStage`-annotated methods of the stage are called
4.3. The steps of the stage are executed
4.4. The `@AfterStage`-annotated methods of the stage are called
4.5. The values of all scenario state fields are extracted.
5. The `@AfterScenario`-annotated methods of all stages are called.
6. The `after()` methods of all scenario rules of all stages are called.


## @BeforeScenario / @AfterScenario

### @BeforeScenario

Methods annotated with `@BeforeScenario` are executed before any step of any stage of a scenario.
This is useful for some general scenario setup. As an example you could initialize a WebDriver instance for Selenium tests or setup a database connection.
Note that such methods are executed *before* injection of values. Thus the code cannot depend on scenario state fields.

{% highlight java %}
public class MyStage {
    @ProvidedScenarioState
    protected WebDriver webDriver;

    @BeforeScenario
    public void startBrowser() {
        webDriver = new HtmlUnitDriver( true );
    }
}
{% endhighlight %}


### @AfterScenario
Analogous to `@BeforeScenario` there is the `@AfterScenario` annotation to execute a methods when a scenario has finished.

{% highlight java %}
@AfterScenario
public void closeBrowser() {
    webDriver.close();
}
{% endhighlight %}



## @ScenarioRule
`@BeforeScenario` and `@AfterScenario` methods are often used to setup and tear down some resource. If you want to reuse such a resource in another stage, you would have to copy these methods into that stage. To avoid that, JGiven offers a concept similar to JUnit rules. The idea is to have a separate class that only contains the before and after methods. This class can then be reused in multiple stages.

A rule class can be any class that provides the methods `before()` and `after()`. This is compatible with JUnit's `ExternalResource` class, which means that you can use classes like `TemporaryFolder` from JUnit directly in JGiven.

### Example

{% highlight java %}
public class WebDriverRule {

   protected WebDriver webDriver;

   public void before() {
        webDriver = new HtmlUnitDriver( true );
   }

   public void after() {
        webDriver.close();
   }
}

public class MyStage {
   @ScenarioRule
   protected WebDriverRule webDriverRule = new WebDriverRule();
}
{% endhighlight %}


## @BeforeStage / @AfterStage

### @BeforeStage

Methods annotated with `@BeforeStage` are executed after injection, but before the first step method is called.
This is useful for setup code that is required by all or most steps of a stage.

{% highlight java %}
@BeforeStage
public void setup() {

}
{% endhighlight %}

### @AfterStage

Analogous to `@BeforeStage` methods, methods can be annotated with `@AfterStage`. These methods are executed after all steps of a stage have been executed, but before extracting the values of scenario state fields. Thus you can prepare state for following stages in an `@AfterStage` method. A typical use case for this is to apply the builder pattern to build a certain object using step methods and build the final object in an `@AfterStage` method.

#### Example

{% highlight java %}

public class MyStage {

   protected CustomerBuilder customerBuilder;

   @ProvidedScenarioState
   protected Customer customer;

   public MyStage a_customer() {
       customerBuilder = new CustomerBuilder();
       return this;
   }

   public MyStage the_customer_has_name( String name ) {
       customerBuilder.withName( name );
       return this;
   }

   @AfterStage
   public void buildCustomer() {
       if (customerBuilder != null) {
           customer = customerBuilder.build();
       }
   }

}
{% endhighlight %}


Back: [Stages and Stage Injection]({{site.baseurl}}/docs/stages/) - Next: [Parameterized Steps]({{site.baseurl}}/docs/parameterizedsteps/)
