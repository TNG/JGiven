import java.util.*

plugins {
    id("jgiven-publishing")
    id("java-library")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

description = "Module for writing JGiven tests with JUnit 6 and JUnit 5"

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
    options.encoding = "UTF-8"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    finalizedBy("jgivenHtml5Report")
}

dependencies {
    api(project(":jgiven-core"))

    implementation(platform(libs.junit.bom))
    compileOnly("org.junit.jupiter:junit-jupiter-params")
    compileOnly("org.junit.jupiter:junit-jupiter-api")

    testImplementation(project(":jgiven-html5-report"))
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly(libs.junit.platform.launcher)
}

val generatedSourceDir = "generatedSrc/java"

tasks.named<Delete>("clean") {
    delete(generatedSourceDir)
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/main/java", generatedSourceDir))
        }
    }
}

file("../jgiven-core/src/main/translations").listFiles()?.forEach { translationFile ->
    if (!translationFile.isFile()) {
        return@forEach
    }
    
    val pkg = translationFile.nameWithoutExtension

    val props = Properties()
    translationFile.inputStream().use { stream -> props.load(stream) }
    props["pkg"] = pkg

    val taskName = "${pkg}Translation"

    val copyTask = tasks.register<Copy>(taskName) {
        from("src/main/templates")
        into("$generatedSourceDir/com/tngtech/jgiven/junit6/lang/$pkg")
        rename("SimpleScenarioTest.template", "${props.getProperty("simple_scenario_test_class")}.java")
        rename("ScenarioTest.template", "${props.getProperty("scenario_test_class")}.java")
        props["module"] = "junit6"
        @Suppress("UNCHECKED_CAST") // Properties has string keys!
        expand(props.toMap() as Map<String, *>)
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
