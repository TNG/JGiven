---
layout: page
title: Attachments
permalink: /docs/attachments/
---

Steps can have attachments. This is useful, for example, to save sceenshots during tests or to refer to larger files that should not be printed inline in the report.

## Creating Attachments

There are several ways to create an attachment by using static factory methods of the [`Attachment`](http://jgiven.org/javadoc/com/tngtech/jgiven/attachment/Attachment.html) class. For example, you can create textual attachments from a given `String` by using the `fromText` method.

{% highlight java %}
Attachment attachment = Attachment.fromText("Hello World", MediaType.PLAIN_TEXT);
{% endhighlight java %}

### Titles

Attachments can have an optional title which can be used by reports, for example, to show a tooltip. The title is set with the `withTitle` method:

{% highlight java %}
Attachment attachment = Attachment
    .fromText("Hello World", MediaType.PLAIN_TEXT)
    .withTitle("Some Title");
{% endhighlight java %}


### Binary Attachments

Binary attachments are internally stored by JGiven as Base64-encoded strings. If the binary content is already present as a Base64-encoded string one can just use that to create a binary attachment:

{% highlight java %}
Attachment attachment = Attachment
    .fromBase64("SGVsbG8gV29ybGQK", MediaType.application("jgiven"));

{% endhighlight java %}


## Adding Attachments to Steps

To add an attachment to a step you have to first inject the [`CurrentStep`](http://jgiven.org/javadoc/com/tngtech/jgiven/CurrentStep.html) class into your stage class by using `@ExpectedScenarioState`.

{% highlight java %}
@ExpectedScenarioState
CurrentStep currentStep;
{% endhighlight java %}

Now you can use `currentStep` inside step methods to add attachments using the [`addAttachment`](http://jgiven.org/javadoc/com/tngtech/jgiven/CurrentStep.html#addAttachment%28com.tngtech.jgiven.attachment.Attachment%29) method. The method takes as argument an instance of [`Attachment`](http://jgiven.org/javadoc/com/tngtech/jgiven/attachment/Attachment.html).

{% highlight java %}
public SELF my_step_with_attachment() {
    Attachment attachment = ...
    currentStep.addAttachment( attachment );
    return self();
}
{% endhighlight java %}

## Example: Taking Screenshots with Selenium WebDriver

If you are using Selenium WebDriver and want to add screenshots to a JGiven step you can do so as follows:

{% highlight java %}
String base64 = ( (TakesScreenshot) webDriver ).getScreenshotAs( OutputType.BASE64 );
currentStep.addAttachment( Attachment.fromBase64( base64, MediaType.PNG )
           .withTitle( "Screenshot" ) );
{% endhighlight java %}

For a full example, see the [Html5ReportStage](https://github.com/TNG/JGiven/blob/master/jgiven-tests/src/test/java/com/tngtech/jgiven/report/html5/Html5ReportStage.java) class that is used by the JGiven tests.

Back: [Tags]({{site.baseurl}}/docs/tags/) - Next: [Spring]({{site.baseurl}}/docs/spring/)
