plugins {
    checkstyle
}

checkstyle {
    toolVersion = "11.1.0"
    configFile = file("${rootProject.projectDir}/checkstyle.xml")

    isShowViolations = true
    isIgnoreFailures = true
}