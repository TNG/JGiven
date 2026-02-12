plugins {
    checkstyle
}

checkstyle {
    toolVersion = "13.2.0"
    configFile = file("${rootProject.projectDir}/checkstyle.xml")

    isShowViolations = true
    isIgnoreFailures = true
}
