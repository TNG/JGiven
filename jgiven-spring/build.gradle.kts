plugins {
    id("jgiven-publishing")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

description = "Module for using Spring dependency injection together with JGiven"

dependencies {
    implementation(project(":jgiven-core"))

    compileOnly(libs.bundles.spring.compile)

    testImplementation(libs.bundles.spring.test)
    testImplementation(libs.hypersql.database)
    testImplementation(libs.bundles.aspectj.spring.test)
}
