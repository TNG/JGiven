---
layout: page
title: Installation
permalink: /docs/installation/
---

The installation of JGiven depends on the build and testing framework that you are using. JGiven works with JUnit and TestNG.

## JUnit

If you are using JUnit, you must depend on the `jgiven-junit` artifact.
Note that `jgiven-junit` does not directly depend on JUnit, thus you also must have a dependency to JUnit itself.
JGiven requires at least JUnit v4.9, while the recommended version is v4.11.

### Maven Dependency

```
<dependency>
   <groupId>com.tngtech.jgiven</groupId>
   <artifactId>jgiven-junit</artifactId>
   <version>{{ site.version }}</version>
   <scope>test</scope>
</dependency>
```

### Gradle Dependency

```
dependencies {
...
   testCompile 'com.tngtech.jgiven:jgiven-junit:{{ site.version }}'
...
}
```

## TestNG

If you are using TestNG, you must depend on the `jgiven-testng` artifact.
Note that `jgiven-testng` does not directly depend on TestNG, thus you also must have a dependency to TestNG itself.

### Maven Dependency

```
<dependency>
   <groupId>com.tngtech.jgiven</groupId>
   <artifactId>jgiven-testng</artifactId>
   <version>{{ site.version }}</version>
   <scope>test</scope>
</dependency>
```

### Gradle Dependency

```
dependencies {
...
   testCompile 'com.tngtech.jgiven:jgiven-testng:{{ site.version }}'
...
}
```

## HTML5 Report

In order to generate an HTML report, you have to add the following dependency in addition to the above dependency.

### Maven Dependency

```
<dependency>
   <groupId>com.tngtech.jgiven</groupId>
   <artifactId>jgiven-html5-report</artifactId>
   <version>{{ site.version }}</version>
   <scope>test</scope>
</dependency>
```

### Gradle Dependency

```
dependencies {
...
   testCompile 'com.tngtech.jgiven:jgiven-html5-report:{{ site.version }}'
...
}
```

## Java Compiler Note

Note that you should compile your test classes with all debugging information (`javac -g`). Otherwise JGiven cannot obtain the parameter names of step methods and will generate names of the form `argX` instead.

Next: [Getting Started]({{site.baseurl}}/docs/gettingstarted/)
