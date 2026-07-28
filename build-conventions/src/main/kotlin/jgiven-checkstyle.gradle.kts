plugins {
    checkstyle
}

checkstyle {
    toolVersion = "13.9.0"
    configFile = file("${rootProject.projectDir}/checkstyle.xml")

    isShowViolations = true
    isIgnoreFailures = true
}
