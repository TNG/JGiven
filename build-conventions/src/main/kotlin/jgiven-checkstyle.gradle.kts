plugins {
    checkstyle
}

checkstyle {
    toolVersion = "13.11.0"
    configFile = file("${rootProject.projectDir}/checkstyle.xml")

    isShowViolations = true
    isIgnoreFailures = true
}
