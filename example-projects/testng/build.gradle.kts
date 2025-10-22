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
}

apply(plugin = "com.tngtech.jgiven.gradle-plugin")

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
    options.encoding = "UTF-8"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    val version: String by project
    testImplementation("com.tngtech.jgiven:jgiven-testng:$version")
    testImplementation("org.testng:testng:7.11.0")
}

tasks.test {
    useTestNG {
        parallel = "methods"
        threadCount = 10
    }
    finalizedBy("jgivenTestReport")
}
