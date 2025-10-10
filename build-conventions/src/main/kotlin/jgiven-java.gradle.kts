import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    java
    jacoco
}


tasks.withType<Test> {
    systemProperty("jgiven.report.dir", "build/reports/jgiven/json")
    systemProperty("jgiven.report.text", "false")

    testLogging {
        showStandardStreams = true
    }
}

tasks.withType<JavaCompile>().configureEach {
    // needed for DeSzenarioTest.java as it has Umlauts in the code
    options.encoding = "UTF-8"
}

tasks.withType<Jar>().configureEach {
    val now = LocalDateTime.now()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
    val yearFormatter = DateTimeFormatter.ofPattern("yyyy")

    manifest {
        attributes(
            "Built-By" to "Gradle ${gradle.gradleVersion}",
            "Build-Date" to now.format(dateFormatter),
            "Copyright" to "2013-${now.format(yearFormatter)} TNG Technology Consulting GmbH",
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "TNG Technology Consulting GmbH",
            "License" to "Apache License v2.0, January 2004",
            "Specification-Title" to project.name,
            "Specification-Version" to project.version,
            "Specification-Vendor" to "TNG Technology Consulting GmbH",
            "Automatic-Module-Name" to "com.tngtech.jgiven.${project.name.replace("-", ".").replace("jgiven.", "")}"
        )
    }
}

normalization {
    runtimeClasspath {
        ignore("META-INF/MANIFEST.MF")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


tasks.withType<Javadoc> {
//     exclude("**/impl/**")
}

tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required.set(true) // coveralls plugin depends on xml format report
    }
}
