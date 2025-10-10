plugins {
    id("jgiven-publishing")
    id("jgiven-checkstyle")
    id("jgiven-java")
}

dependencies {
    implementation(project(":jgiven-core"))
    testImplementation(project(":jgiven-junit"))
    testImplementation(project(":jgiven-spring-junit4"))
    testImplementation(project(":jgiven-tests"))
    testImplementation(project(":jgiven-html5-report"))
    testImplementation(project(":jgiven-testng"))
    testImplementation("org.seleniumhq.selenium:htmlunit-driver:4.13.0")
    testImplementation(libs.bundles.spring.compile)
    testImplementation(junitVariableVersionLibs.junit4.params)
    testImplementation(libs.testng)
}

tasks.named<Test>("test") {
    finalizedBy("jgivenPlainTextReport")
    finalizedBy("jgivenHtml5Report")
    finalizedBy("jgivenAsciiDocReport")
    // there are tests that fail on purpose to show a failing test in the report
    ignoreFailures = true
}
