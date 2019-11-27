package com.tngtech.jgiven.example.projects.junit5

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import com.tngtech.jgiven.annotation.JGivenConfiguration
import com.tngtech.jgiven.annotation.ScenarioStage
import com.tngtech.jgiven.junit5.JGivenExtension

@ExtendWith(JGivenExtension::class)
@JGivenConfiguration(JGivenTestConfiguration::class)
class JUnit5Test {

    @ScenarioStage
    lateinit var givenStage: GivenStage

    @ScenarioStage
    lateinit var whenStage: WhenStage

    @ScenarioStage
    lateinit var thenStage: ThenStage

    @Test
    @Tag("JUnit5 Tag")
    fun scenario_with_JUnit5() {
        givenStage.given().message("Hello JUnit")
        whenStage.`when`().handle_message()
        thenStage.then().the_result_is("Hello JUnit 5!")
    }
}
