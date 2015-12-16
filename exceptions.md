---
layout: page
title: Exception Handling
permalink: /docs/exceptions/
---

When writing JGiven scenarios you should know how JGiven handles exceptions. In general, JGiven captures all exceptions that are thrown in step methods. This is done, so that the steps following the errornous step can still be executed by JGiven to show them in the report as skipped steps. After the whole scenario has been executed, JGiven will rethrow the exception that has been previously thrown, so that the overall test actually fails. This behavior is in general no problem as long as you do not really expect exceptions to happen.

## Expecting Exceptions

Let's assume you want to verify that in the step method `doing_some_action` a certain exception is thrown and you write the following scenario using JUnit:

{% highlight java %}
// DOES NOT WORK!
@Test(expected = MyExpectedException.class)
public void an_expected_exception_should_be_thrown() {
    given().some_errornous_precondition();
    when().doing_some_action();
}
{% endhighlight java %}

This scenario has two drawbacks: first it will not work and second the generated report will not make clear that actually an exception is expected. It will not work, because the JUnit mechanism to check for an expected exception actually comes too early. As already explained above, JGiven will throw the exception itself, after the scenario has finished. As JGiven is actually implemented as a JUnit rule, throwing this exception will be after JUnit has checked for an expected exception. This technical issue can be fixed by using the `ExpectedException` rule of JUnit:

{% highlight java %}
@Rule
public ExpectedException rule = ExpectedException.none();

@Test
public void an_expected_exception_should_be_thrown() {
   // will work, but is not visible in the report
   rule.expect(MyExpectedException.class);
   given().some_errornous_precondition();
   when().doing_some_action();
}
{% endhighlight java %}

In this case, the actual verification of the exception is done after JGiven has thrown the exception and thus the `ExpectedException` rule will get the exception.

### A better approach

The above example has still the big disadvantage that it will not visible in the report that an exception is actually expected. More helpful would be the following scenario:

{% highlight java %}
@Test
public void an_expected_exception_should_be_thrown() {
   given().some_errornous_precondition();
   when().doing_some_action();
   then().an_exception_is_thrown();
}
{% endhighlight java %}

Note, that this is scenario is still very technical and you should consider replacing the word 'exception' with a more domain specific term.

In order to realize the above scenario you have to explictly catch the exception in the `doing_some_action` step and store it into a scenario state field.

{% highlight java %}
@ProvidedScenarioState
MyExpectedException someExpectedException;

public SELF doing_some_action() {
   try {
       ...
   } catch (MyExpectedException e) {
      someExpectedException = e;
   }
   return self();
}
{% endhighlight java %}

In the When-Stage you then just have to check whether the field is set:

{% highlight java %}
@ExpectedScenarioState
MyExpectedException someExpectedException;

public SELF an_exception_is_thrown() {
    assertThat( someExpectedException ).isNotNull();
    return self();
}
{% endhighlight java %}

Back: [Attachments]({{site.baseurl}}/docs/attachments/) - Next: [Spring]({{site.baseurl}}/docs/spring/)
