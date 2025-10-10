plugins {
    id("jgiven-publishing")
    id("java-library")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

description = "Module for writing JGiven tests with JUnit 5"

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
    testImplementation("org.junit.platform:junit-platform-runner")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}
