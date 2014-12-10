---
layout: page
title: Installation
permalink: /docs/installation/
---

The installation of JGiven depends on the build tool that you are using.

## Maven

### Maven Dependency

Depending on the testing framework you need either te `jgiven-junit` or the `jgiven-testng` dependency.


#### JUnit
```
<dependency>
   <groupId>com.tngtech.jgiven</groupId>
   <artifactId>jgiven-junit</artifactId>
   <version>{{ site.version }}</version>
   <scope>test</scope>
</dependency>
```

Note that `jgiven-junit` does not directly depend on JUnit, thus you also must have a dependency to JUnit itself.
JGiven requires at least JUnit v4.9, while the recommended version is 4.11.

#### TestNG
```
<dependency>
   <groupId>com.tngtech.jgiven</groupId>
   <artifactId>jgiven-testng</artifactId>
   <version>{{ site.version }}</version>
   <scope>test</scope>
</dependency>
```

Note that `jgiven-testng` does not directly depend on TestNG, thus you also must have a dependency to TestNG itself.

#### HTML5 Report
JGiven provides a basic static HTML report. In order to get a model HTML5-based report you have to add the following dependency:

```
<dependency>
   <groupId>com.tngtech.jgiven</groupId>
   <artifactId>jgiven-html5-report</artifactId>
   <version>{{ site.version }}</version>
   <scope>test</scope>
</dependency>
```


Next: [Getting Started]({{site.baseurl}}/docs/gettingstarted/)
