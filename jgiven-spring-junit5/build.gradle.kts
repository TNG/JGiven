plugins {
    id("java-library")
    id("jgiven-publishing")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

description = "Module for using Spring dependency injection together with JGiven and JUnit 5 (Deprecated: Use jgiven-spring-junit6 for Spring 7 support)"

val junit5BomVersion = rootProject.findProperty("JUnit5Version")?.toString()
    ?: rootProject.findProperty("junit5Version")?.toString()
    ?: extensions.getByType<VersionCatalogsExtension>()
        .named("libs")
        .findVersion("junit5-version")
        .get()
        .requiredVersion

dependencies {
    api(project(":jgiven-spring"))
    api(project(":jgiven-junit5"))

    implementation(enforcedPlatform("org.junit:junit-bom:$junit5BomVersion"))
    testImplementation(enforcedPlatform("org.junit:junit-bom:$junit5BomVersion"))

    compileOnly(libs.bundles.spring.compile)
    compileOnly("org.junit.jupiter:junit-jupiter-api")

    testImplementation(project(":jgiven-html5-report"))
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.bundles.spring.test)
    testImplementation(libs.hypersql.database)
    testImplementation(libs.bundles.aspectj.spring.test)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    finalizedBy("jgivenHtml5Report")
}
