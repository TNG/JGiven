---
layout: page
title: Tags
permalink: /docs/tags/
---

Tags are used to organize scenarios. A tag in JGiven is just a Java annotation that is itself annotated with the [`@IsTag`]({{baseurl}}/javadoc/com/tngtech/jgiven/annotation/IsTag.html) annotation. You can annotate whole test classes or single test methods with tag annotations. Tags then appear in the resulting report.

Let's say you want to know which scenarios covers the _coffee_ feature. To do so you define a new Java annotation:

{% highlight java %}
@IsTag
@Retention( RetentionPolicy.RUNTIME )
public @interface CoffeeFeature {}
{% endhighlight java %}

Two thinks are important:
1. The annotation itself must be annotated with the `@IsTag` annotation to mark it as a JGiven tag.
2. The annotation must have retention policy `RUNTIME` so that JGiven can recognize it at runtime.

To tag a scenario with the new tag, you just annotate the corresponding test method:

{% highlight java %}
import com.tngtech.jgiven.annotation.IsTag;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Test @CoffeeFeature
public void coffee_is_made_when_pressing_the_make_coffee_button() {
   ...
}
{% endhighlight java %}

In the report the scenario will then be tagged with tag `CoffeeFeature`.

You can also annotate the whole test class, in which case all scenarios of that class will get the tag.

### Descriptions
Tags can have descriptions. These descriptions appear in the report on the corresponding page for the tag. For example:

{% highlight java %}
@IsTag( description = "In order to be refreshed, as a customer, I want coffee" )
@Retention( RetentionPolicy.RUNTIME )
public @interface CoffeeFeature {}
{% endhighlight java %}

### Overriding the Name
It is possible to override the name of a tag by using the `type` attribute of the `IsTag` annotation. This allows you to have a different name for the tag than the actual type of the annotation. For example, if you want to have a tag `Feature: Coffee` you can define the `CoffeeFeature` annotation as follows:

{% highlight java %}
@IsTag( type = "Feature: Coffee" )
@Retention( RetentionPolicy.RUNTIME )
public @interface CoffeeFeature { }
{% endhighlight java %}


### Values
Sometimes you do not want to always create a new annotation for each tag. Let's say you organize your work in stories and for each story you want to know which scenarios have been written for that story. Instead of having a separate annotation for each story you can define a `Story` annotation with a `value()` method:

{% highlight java %}
@IsTag
@Retention( RetentionPolicy.RUNTIME )
public @interface Story {
    String value();
}
{% endhighlight java %}


Annotations with different values are treated as different tags in JGiven. So using the above annotation you can now mark scenarios to belong to certain stories:

{% highlight java %}
@Test @Story("ACME-123")
public void scenarios_can_have_tags() {
  ...
}
{% endhighlight java %}

In the report the tag will now be `ACME-123` instead of `Story`.

If you want that the type is prepended to the value in the report you can set the `prependType` attribute of the `IsTag` annotation to `true`. In this case the tag will be shown as `Story-ACME-123`.
Note that this feature works in combination with the `type` attribute.

Annotations with the same type but different values are grouped in the report. E.g. multiple `@Story` tags with different values will be grouped under `Story`.

Note that you currently can *not* have different descriptions for different values.

#### Array Values
The value of a tag annotation can also be an array:

{% highlight java %}
@IsTag
@Retention( RetentionPolicy.RUNTIME )
public @interface Story {
    String[] value();
}
{% endhighlight java %}

This allows you to give the same scenario multiple tags of the same type with different values:
{% highlight java %}
@Test @Story( {"ACME-123", "ACME-456"} )
public void scenarios_can_have_tags() {
  ...
}
{% endhighlight java %}

For each value, one tag will be generated, e.g. `ACME-123` and `ACME-456`. If you do not want that behavior you can set the `explodeArray` attribute of `@IsTag` to `false`, in that case only one tag will be generated and the values will comma-separated, e.g. `ACME-123,ACME-456`.

### Overriding the Value
If you want to group several annotations with different types under a common name. You can combine the `type` attribute with the `value` attribute as follows:
{% highlight java %}
@IsTag( type = "Feature", value = "Tag" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureTags { }
{% endhighlight java %}

The tag will appear as `Tag` in the report and all annotations with type `Feature` will be grouped together.


Back: [Parameters]({{site.baseurl}}/docs/parameters/)
