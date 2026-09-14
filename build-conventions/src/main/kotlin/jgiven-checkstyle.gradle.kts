plugins {
    checkstyle
}

checkstyle {
    toolVersion = "14.1.0"
    configFile = file("${rootProject.projectDir}/checkstyle.xml")

    isShowViolations = true
    isIgnoreFailures = true
}
