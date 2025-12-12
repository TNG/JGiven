plugins {
    id("java-library")
    id("jgiven-publishing")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

dependencies {
    implementation(libs.guava)
    implementation(libs.slf4j.api)
    implementation(project(":jgiven-core"))
}
