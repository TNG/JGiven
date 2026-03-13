plugins {
    id("java-library")
    id("jgiven-publishing")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

description = "Module for using Spring dependency injection together with JGiven and JUnit 5"

dependencies {
    api(project(":jgiven-spring"))
    api(project(":jgiven-junit5"))

    if (rootProject.hasProperty("junitVersion")) {
        implementation(platform("org.junit:junit-bom:${rootProject.property("junitVersion")}"))
    } else {
        implementation(platform(libs.junit.bom))
    }

    compileOnly(libs.bundles.spring.compile)
    compileOnly("org.junit.jupiter:junit-jupiter-api")

    testImplementation(project(":jgiven-html5-report"))
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.bundles.spring.test)
    testImplementation(libs.hypersql.database)
    testImplementation(libs.bundles.aspectj.spring.test)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    finalizedBy("jgivenHtml5Report")
}
