plugins {
    java
    id("jgiven-checkstyle")
    id("jgiven-java")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    testImplementation(project(":jgiven-core"))
    testImplementation(project(":jgiven-junit5"))
    testImplementation(project(":jgiven-html5-report"))

    implementation(platform(libs.junit.bom))

    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("jgivenHtml5Report")
}
