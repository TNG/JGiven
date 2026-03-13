plugins {
    id("java-library")
    id("groovy")
    id("jgiven-publishing")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

description = "Module for writing JGiven tests with Spock 2"

val spock2Version = "2.4-groovy-5.0"
val groovyVersion = "4.0.30"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api(project(":jgiven-junit"))
    implementation("org.apache.groovy:groovy:$groovyVersion")
    implementation("org.spockframework:spock-core:$spock2Version")
    implementation("org.spockframework:spock-junit4:$spock2Version")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.byteBuddy)
    testImplementation(project(":jgiven-html5-report"))
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("jgivenHtml5Report")
}
