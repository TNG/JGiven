plugins {
    checkstyle
}

checkstyle {
    toolVersion = "12.1.0"
    configFile = file("${rootProject.projectDir}/checkstyle.xml")

    isShowViolations = true
    isIgnoreFailures = true
}
