plugins {
    id("jgiven-publishing")
    id("java-library")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

import java . util . Properties

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


file("../jgiven-core/src/main/translations").listFiles()?.forEach { translationFile ->
    val pkg = translationFile.name.split(".")[0]

    val props = Properties()
    translationFile.inputStream().use { stream -> props.load(stream) }
    props["pkg"] = pkg

    val taskName = "${pkg}Translation"

    val copyTask = tasks.register<Copy>(taskName) {
        from("src/main/templates")
        into("$generatedSourceDir/com/tngtech/jgiven/junit/lang/$pkg")
        rename("SimpleScenarioTest.template", "${props.getProperty("simple_scenario_test_class")}.java")
        rename("ScenarioTest.template", "${props.getProperty("scenario_test_class")}.java")
        expand(props as Map<String, Any>)
        filteringCharset = "UTF-8"
    }

    tasks.named("compileJava") {
        dependsOn(copyTask)
    }

    project.afterEvaluate {
        tasks.named("sourcesJar") {
            dependsOn(copyTask)
        }
    }
}
