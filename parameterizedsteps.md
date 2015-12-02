---
layout: page
title: Parameterized Steps
permalink: /docs/parameterizedsteps/
---

Step methods can have parameters. Parameters are formatted in reports by using the `String.valueOf` method, applied to the arguments. The formatted arguments are added to the end of the step description.

{% highlight java %}
given().the_ingredient( "flour" ); // Given the ingredient flour
given().multiple_arguments( 5, 6 ); // Given multiple arguments 5 6
{% endhighlight %}

## Parameters within a sentence

To place parameters within a sentence instead the end of the sentence you can use the `$` character.

{% highlight java %}
given().$_eggs( 5 );
{% endhighlight %}

In the generated report `$` is replaced with the corresponding formatted parameter. So the generated report will look as follows:

{% highlight java %}
Given 5 eggs
{% endhighlight %}

If there are more parameters than `$` characters, the remaining parameters are added to the end of the sentence.

If a `$` should not be treated as a placeholder for a parameter, but printed verbatim, you can write `$$`, which will appear as a single `$` in the report.

## Parameter Formatting

Sometimes the `toString()` representation of a parameter object does not fit well into the report. In these cases you have three possibilities:

1. Change the `toString()` implementation. This is often not possible or not desired, because it requires the modification of production code. However, sometimes this is appropriate.
2. Provide a wrapper class for the parameter object that provides a different `toString()` method. This is useful for parameter objects that you use very often.
3. Change the formatting of the parameter by using special JGiven annotations. This can be used in all other cases and also to change the formatting of primitive types.

### The `@Format` annotation

The default formatting of a parameter can be overridden by using the `@Format` annotation. It takes as a parameter a class that implements the `ArgumentFormatter` interface. In addition, an optional array of arguments can be given to configure the customer formatter.
For example, the built-in `BooleanFormatter` can be used to format `boolean` values:

{% highlight java %}
public SELF the_machine_is_$(
    @Format( value = BooleanFormatter.class, args = { "on", "off" } ) boolean onOrOff ) {
    ...
}
{% endhighlight %}

In this case, `true` values will be formatted as `on` and `false` as `off`.

### Custom formatting annotations

As using the `@Format` annotation is often cumbersome, especially if the same formatter is used in multiple places, one can define and use custom formatting annotations instead.

An example is the pre-defined `@Quoted` annotation, which surrounds parameters with quotation marks. The annotation is defined as follows:

{% highlight java %}
@Format( value = PrintfFormatter.class, args = "\"%s\"" )
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER, ElementType.ANNOTATION_TYPE } )
public @interface Quoted {}
{% endhighlight %}

As you can see, the annotation itself is annotated with the `@Format` annotation as described above, which will be applied to all parameters that are annotated with `@Quoted`.

#### Example

For example, given the following step method:

{% highlight java %}
public SELF the_message_$_is_printed_to_the_console( @Quoted message ) { ... }
{% endhighlight %}

When invoked as

{% highlight java %}
then().the_message_$_is_printed_to_the_console( "Hello World" );
{% endhighlight %}

Then this will result in the report as:

```
Then the message "Hello World" is printed to the console
```

#### The `@AnnotationFormat` annotation

Another pre-defined annotation is the `@Formatf` annotation which uses the `@AnnotationFormat` annotation to specify the formatter. Formatters of this kind implement the `AnnotationArgumentFormatter` interface. This allows for very flexible formatters that can take the concrete arguments of the annotation into account.

{% highlight java %}
@AnnotationFormat( value = PrintfAnnotationFormatter.class )
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.PARAMETER )
public @interface Formatf {
    String value() default "%s";
}
{% endhighlight %}

## Tables as Parameters

Sometimes information can be represented very concisely by using tables. JGiven supports this with the `@Table` annotation for step parameters. Such parameters are then formatted as tables in the report. The types of such parameters can be:

1. A list of lists, where each inner list represents a single row and the first row represents the headers of the table.
2. A list of POJOs, where each POJO represents a row and the headers are inferred by the names of the fields of the POJO.
3. A single POJO, which is equivalent to a one-element list of POJOs.

### Example

Given the following POJO:

{% highlight java %}
class CoffeeWithPrice {
   String name;
   double price_in_EUR;
   CoffeeWithPrice(String name, double priceInEur) {
      this.name = name;
      this.price_in_EUR = priceInEur;
   }
}
{% endhighlight %}

Then you can define a step method as follows:

{% highlight java %}
public SELF the_prices_of_the_coffees_are( @Table CoffeeWithPrice... prices ) {
  ...
}
{% endhighlight %}

Finally, the step method can be called with a list of arguments:

{% highlight java %}
given().the_prices_of_the_coffees_are(
   new CoffeeWithPrice("Espresso", 2.0),
   new CoffeeWithPrice("Cappuccino", 2.5));
{% endhighlight %}

Then the report will look as follows:

```
Given the prices of the coffees are

      | name       | price in EUR |
      +------------+--------------+
      | Espresso   | 2.0          |
      | Cappuccino | 2.5          |
```

For additional options, see the [JavaDoc documentation of the `@Table` annotation]({{site.baseurl}}/javadoc/com/tngtech/jgiven/annotation/Table.html)

Back: [Life-Cycle Methods]({{site.baseurl}}/docs/lifecycle/) - Next: [Parameterized Scenarios]({{site.baseurl}}/docs/parameterizedscenarios/)
