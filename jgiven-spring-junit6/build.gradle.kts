plugins {
    id("java-library")
    id("jgiven-publishing")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

description = "Module for using Spring dependency injection together with JGiven and JUnit 6/5"

dependencies {
    api(project(":jgiven-spring"))
    api(project(":jgiven-junit6"))
    
    implementation(platform(libs.junit.bom))
    
    compileOnly(libs.spring7.context)
    compileOnly(libs.spring7.test)
    compileOnly("org.junit.jupiter:junit-jupiter-api")

    testImplementation(project(":jgiven-html5-report"))
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation("org.springframework:spring-context:7.0.6")
    testImplementation("org.springframework:spring-test:7.0.6")
    testImplementation("org.springframework:spring-tx:7.0.6")
    testImplementation("org.springframework:spring-jdbc:7.0.6")
    testImplementation(libs.hypersql.database)
    testImplementation(libs.bundles.aspectj.spring.test)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    finalizedBy("jgivenHtml5Report")
}
