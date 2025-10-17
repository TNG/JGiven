buildscript {
    repositories {
        if (project.hasProperty("staging")) {
            maven(url = "https://oss.sonatype.org/content/repositories/staging/")
        }
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath("com.tngtech.jgiven:jgiven-gradle-plugin:${"version"}")
    }
}

plugins {
    java
    idea
    eclipse
    id("com.tngtech.jgiven.gradle-plugin")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/groups/public/")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
    options.compilerArgs.add("-parameters")
    options.encoding = "UTF-8"
}

dependencies {
    val version: String by project
    testImplementation("com.tngtech.jgiven:jgiven-junit:$version")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.36.0")
    testImplementation("io.github.bonigarcia:webdrivermanager:6.3.2")
    testImplementation("org.assertj:assertj-core:3.27.6")
}

tasks.test {
    finalizedBy("jgivenTestReport")
}
