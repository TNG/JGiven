buildscript {
    repositories {
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
    groovy
}

apply(plugin = "com.tngtech.jgiven.gradle-plugin")

description = "Module for writing JGiven tests with Spock"


repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    val version: String by project
    implementation("org.spockframework:spock-core:2.4-groovy-5.0")
    implementation("org.spockframework:spock-junit4:2.4-groovy-5.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.14.2")
    testImplementation("com.tngtech.jgiven:jgiven-spock2:$version")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("jgivenTestReport")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
