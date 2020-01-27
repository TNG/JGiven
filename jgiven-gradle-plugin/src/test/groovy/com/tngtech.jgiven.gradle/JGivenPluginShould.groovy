package com.tngtech.jgiven.gradle


import com.google.common.io.Resources
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.FROM_CACHE
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class JGivenPluginShould extends Specification {

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    def "test is cacheable"() {
        given:
        buildFile << """
        plugins {
            id 'java'
            id 'com.tngtech.jgiven.gradle-plugin'
        }
        
       repositories { jcenter() }
       dependencies {
         testImplementation 'com.tngtech.jgiven:jgiven-junit:1.0.0-RC4' 
         testImplementation 'junit:junit:4.12'
        }
    """

        testProjectDir.newFolder("src", "test", "java")
        File scenario = testProjectDir.newFile("src/test/java/SimpleScenario.java")
        scenario << Resources.toByteArray(Resources.getResource("SimpleScenario.java"))

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("--build-cache", "test", "jgivenTestReport")
                .withPluginClasspath()
                .build()

        then:
        result.task(":test").outcome == SUCCESS
        result.task(":jgivenTestReport").outcome == SUCCESS

        when:
        new File(testProjectDir.root, 'build').deleteDir()
        result = GradleRunner.create()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("--build-cache", "test", "jgivenTestReport")
                .withPluginClasspath()
                .build()


        then:
        result.task(":test").outcome == FROM_CACHE
        result.task(":jgivenTestReport").outcome == FROM_CACHE

    }
}