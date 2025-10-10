plugins {
    id("java-library")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

repositories {
    mavenCentral()
}

description = "Module for injecting automated performance analysis for test methods"

dependencies {
    implementation(project(":jgiven-core"))
    implementation("com.google.guava:guava:33.5.0-jre")
    implementation(libs.byteBuddy.agent)
    implementation(libs.byteBuddy.plugin)

    testImplementation(libs.mockito)
    testImplementation("com.google.guava:guava-testlib:33.5.0-jre")
}

val generatedSourceDir = "generatedSrc/java"

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/main/java", generatedSourceDir))
        }
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Premain-Class" to "com.tngtech.jgiven.timing.TimerInjectorAgent"
        )
    }
}
