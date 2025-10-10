plugins {
    id("java-library")
    id("jgiven-publishing")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

description = "Module for using Spring dependency injection together with JGiven and JUnit 4"

dependencies {
    api(project(":jgiven-spring"))
    api(project(":jgiven-junit"))

    compileOnly(junitVariableVersionLibs.junit4)
    compileOnly(libs.bundles.spring.compile)

    testImplementation(junitVariableVersionLibs.junit4)
    testImplementation(libs.bundles.spring.test)
    testImplementation(libs.hypersql.database)
    testImplementation(libs.bundles.aspectj.spring.test)
}
