plugins {
    checkstyle
}

checkstyle {
    toolVersion = "13.3.0"
    configFile = file("${rootProject.projectDir}/checkstyle.xml")

    isShowViolations = true
    isIgnoreFailures = true
}
