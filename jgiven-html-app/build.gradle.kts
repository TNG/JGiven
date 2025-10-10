plugins {
    id("jgiven-publishing")
    id("com.github.node-gradle.node")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

val htmlAppVersion = "1.1.3"

node {
    download = true
}

tasks.register<com.github.gradle.node.npm.task.NpmTask>("npmPack") {
    args.set(listOf("pack", "jgiven-html-app@$htmlAppVersion"))

    doLast {
        copy {
            from(tarTree(resources.gzip("jgiven-html-app-$htmlAppVersion.tgz")))
            into(layout.buildDirectory)
        }
    }
}

tasks.register<Zip>("zipAppDir") {
    dependsOn("npmPack")
    from(layout.buildDirectory.dir("package/dist"))
    archiveFileName.set("app.zip")
    destinationDirectory.set(layout.buildDirectory.dir("resources/main/com/tngtech/jgiven/report/html5"))
}

tasks.named("jar") {
    dependsOn("zipAppDir")
}
