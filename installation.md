---
layout: page
title: Installation
permalink: /docs/installation/
---

The installation of JGiven depends on the build and unit testing tool that you are using.

## JUnit

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

JGiven provides a basic static HTML report. In order to get a model HTML5-based report you have to add the dependency in addition to the above dependency.

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

Note that you should complile your test classes with all debugging informations (`javac -g`). Otherwise JGiven cannot obtain the parameter names of step methods and will generate names of the form `argX` instead.

### Java 8 Note (JGiven < 0.8.1)

If you are using Java 8 and a JGiven version < 0.8.1, you should compile your test code with the `-parameters` option of the `javac` compiler. Otherwise JGiven cannot obtain the parameter names of step methods and will generate generic names of the form argX instead.

Next: [Getting Started]({{site.baseurl}}/docs/gettingstarted/)
