plugins {
    id("java")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
}

dependencies {
    testImplementation(project(":jgiven-core"))
    testImplementation(project(":jgiven-junit5"))
    testImplementation(project(":jgiven-html5-report"))

    implementation(platform(libs.junit.bom))

    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.platform:junit-platform-runner")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("jgivenHtml5Report")
}
