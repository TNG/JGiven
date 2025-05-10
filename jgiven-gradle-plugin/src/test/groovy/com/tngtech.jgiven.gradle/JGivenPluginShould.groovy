package com.tngtech.jgiven.gradle

import com.google.common.io.Resources
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

import static org.gradle.testkit.runner.TaskOutcome.FROM_CACHE
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class JGivenPluginShould extends Specification {

    @TempDir
    File testProjectDir
    File buildFile

    def setup() {
        buildFile = new File(testProjectDir,'build.gradle')
    }

    def "configuration is cacheable"() {
        given:
        buildFile << """
        plugins {
            id 'java'
            id 'com.tngtech.jgiven.gradle-plugin'
        }
        
       repositories { mavenCentral() }
       dependencies {
         testImplementation 'com.tngtech.jgiven:jgiven-junit:1.3.1' 
         testImplementation 'junit:junit:4.13.2' 
        }
    """

        new File(testProjectDir,"src/test/java").mkdirs()
        File scenario = new File(testProjectDir,"src/test/java/SimpleScenario.java")
        scenario << Resources.toByteArray(Resources.getResource("SimpleScenario.java"))

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withArguments("--configuration-cache", "test", "jgivenTestReport", "-S", "--info"/*, "-Dorg.gradle.debug=true"*/ )
                .withPluginClasspath()
                .build()

        then:
        result.task(":test").outcome == SUCCESS
        result.task(":jgivenTestReport").outcome == SUCCESS
    }

    def "test is cacheable"() {
        given:
        buildFile << """
        plugins {
            id 'java'
            id 'com.tngtech.jgiven.gradle-plugin'
        }
        
       repositories { mavenCentral() }
       dependencies {
         testImplementation 'com.tngtech.jgiven:jgiven-junit:1.3.1' 
         testImplementation 'junit:junit:4.13.2' 
        }
    """

        new File(testProjectDir,"src/test/java").mkdirs()
        File scenario = new File(testProjectDir,"src/test/java/SimpleScenario.java")
        scenario << Resources.toByteArray(Resources.getResource("SimpleScenario.java"))

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withArguments("--build-cache", "test", "jgivenTestReport", "-S", "--info")
                .withPluginClasspath()
                .build()

        then:
        result.task(":test").outcome == SUCCESS
        result.task(":jgivenTestReport").outcome == SUCCESS

        when:
        new File(testProjectDir, 'build').deleteDir()
        result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withArguments("--build-cache", "test", "jgivenTestReport", "-S", "--info")
                .withPluginClasspath()
                .build()


        then:
        result.task(":test").outcome == FROM_CACHE
        result.task(":jgivenTestReport").outcome == FROM_CACHE

    }
}