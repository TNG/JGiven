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
        val version: String by project
        classpath("com.tngtech.jgiven:jgiven-gradle-plugin:$version")
    }
}

plugins {
    java
    idea
    eclipse
}

apply(plugin = "com.tngtech.jgiven.gradle-plugin")

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/groups/public/")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
    options.encoding = "UTF-8"
}

dependencies {
    val version: String by project
    testImplementation("com.tngtech.jgiven:jgiven-junit:$version")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.39.0")
    testImplementation("io.github.bonigarcia:webdrivermanager:6.3.3")
    testImplementation("org.assertj:assertj-core:3.27.7")
}

tasks.test {
    finalizedBy("jgivenTestReport")
}
