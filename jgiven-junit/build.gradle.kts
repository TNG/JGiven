plugins {
    id("jgiven-publishing")
    id("java-library")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

description = "Module for writing JGiven tests with JUnit"

dependencies {
    api(project(":jgiven-core"))
    compileOnly(junitVariableVersionLibs.junit4)
    implementation(project(":jgiven-html5-report"))

    testImplementation(libs.bundles.junit4)
    testImplementation(libs.mockito)
    testImplementation("com.googlecode.junit-toolbox:junit-toolbox:2.4")
}

tasks.named("test") {
    finalizedBy("jgivenHtml5Report")
}

val generatedSourceDir = "generatedSrc/java"

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/main/java", generatedSourceDir))
        }
    }
}
