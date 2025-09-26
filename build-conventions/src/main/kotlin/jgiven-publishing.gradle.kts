plugins {
    id("com.vanniktech.maven.publish")
    id("signing")
}

mavenPublishing {
    publishToMavenCentral(true)
    signAllPublications()

    coordinates(
        "com.tngtech.jgiven",
        project.name,
        rootProject.version.toString()
    )

    pom {
        name = project.name
        url = "https://jgiven.org"
        description = project.description

        scm {
            url = "scm:git@github.com:TNG/jgiven.git"
            connection = "scm:git@github.com:TNG/jgiven.git"
            developerConnection = "scm:git@github.com:TNG/jgiven.git"
        }
        licenses {
            license {
                name = "The Apache Software License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "janschaefer"
                name = "Jan Schäfer"
            }
            developer {
                id = "l-1squared"
                name = "Kristof Karhan"
            }
        }
    }
}

signing {
    // requires gradle.properties, see http://www.gradle.org/docs/current/userguide/signing_plugin.html
    //logger.debug("Task graph at signing:" + gradle.taskGraph.getAllTasks().stream().map(Task::getName).collect(Collectors.toList()));
    setRequired({
        (project.extra.has("isReleaseVersion") && project.extra["isReleaseVersion"] as Boolean) &&
                gradle.taskGraph.getAllTasks().stream().anyMatch({ task -> task is PublishToMavenRepository });
    });


    var signingKey = findProperty("signingKey") as String?
    var signingPassword = findProperty("signingPassword") as String?
    useInMemoryPgpKeys(signingKey, signingPassword)
}