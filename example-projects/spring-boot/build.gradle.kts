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
    id("org.springframework.boot") version "4.0.2"
    java
    idea
    eclipse
}

apply(plugin = "io.spring.dependency-management")
apply(plugin = "com.tngtech.jgiven.gradle-plugin")

tasks.wrapper {
    gradleVersion = "9.3.1"
    distributionType = Wrapper.DistributionType.ALL
}

val version: String by project
val assertjVersion = "3.27.7"
val junitDataproviderVersion = "1.13.1"
val hsqldbVersion = "2.7.4"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("junit:junit:4.13.2")
    implementation("com.h2database:h2:2.4.240")
    testImplementation("com.tngtech.jgiven:jgiven-spring-junit4:$version")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("com.tngtech.java:junit-dataprovider:$junitDataproviderVersion")
    testImplementation("org.hsqldb:hsqldb:$hsqldbVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
}

tasks.test {
    finalizedBy("jgivenTestReport")
}
