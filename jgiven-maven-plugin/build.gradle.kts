plugins {
    id("java-gradle-plugin")
    id("jgiven-publishing")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

description = "JGiven Maven Mojo"

dependencies {
    implementation(project(":jgiven-core"))
    implementation(project(":jgiven-asciidoc-report"))
    implementation(project(":jgiven-html5-report"))
    implementation("org.apache.maven.plugin-tools:maven-plugin-tools-api:3.15.1")
    implementation("org.apache.maven.plugin-tools:maven-plugin-annotations:3.15.2")

    testImplementation(project(":jgiven-html5-report"))

    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.junit.platform:junit-platform-runner")
    testImplementation("org.apache.maven.shared:maven-invoker:3.3.0")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.register<Copy>("copyClasses") {
    dependsOn(tasks.compileJava)
    from("build/classes/java/main")
    into("build/maven/target/classes")
}

tasks.register<Copy>("generatePom") {
    dependsOn("copyClasses")
    from("src/main/maven")
    into("build/maven")
    filesMatching("**/pom.*") {
        expand("version" to project.version)
    }
}

tasks.register<CrossPlatformExec>("generateMavenPlugin") {
    dependsOn("generatePom")
    // currently it seems to be the more or less only clean solution
    // to generate a plugin.xml file to use maven directly
    // if anyone has a better solution please let us know!
    buildCommand(
        "mvn", "-nsu", "-U", "-f", "build/maven/pom.xml", "plugin:descriptor", "--batch-mode",
        "-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
    )
}

tasks.named("generateMavenPlugin") {
    val thisProjectsPublishTasks = tasks.filter { task -> task.name.startsWith("publish") }.toList()
    mustRunAfter(rootProject.allprojects.flatMap { project ->
        project.tasks.filter { task ->
            task.name.startsWith("publish") && !thisProjectsPublishTasks.contains(task)
        }
    })
    onlyIf {
        val publishTasks = gradle.taskGraph.allTasks.filter {
            it.name.lowercase().startsWith("publish")
        }
        logger.debug("TaskGraph at generateMavenPlugin: ${gradle.taskGraph.allTasks.map { it.name }}")
        publishTasks.isNotEmpty()
    }
}

tasks.named<Jar>("jar") {
    dependsOn("generateMavenPlugin")
    dependsOn("generatePom")
    tasks.javadoc.get().dependsOn(this)
    doFirst {
        copy {
            from("build/maven") {
                include("**/pom.*")
                filter { line ->
                    line.replace(Regex("<version>.*</version>"), "")
                }
            }
            into("build/classes/main/META-INF/maven/com.tngtech.jgiven/jgiven-maven-plugin")
            includeEmptyDirs = false
        }
        copy {
            from("build/maven/target/classes") {
                include("**/*.xml")
                exclude("**/*.class")
            }
            into("build/classes/java/main")
        }
    }
}

abstract class CrossPlatformExec : Exec() {
    fun buildCommand(command: String, vararg commandArgs: String) {
        if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
            executable = "cmd"
            args = listOf("/c", command)
        } else {
            executable = command
        }
        args(commandArgs.toList())
    }
}
