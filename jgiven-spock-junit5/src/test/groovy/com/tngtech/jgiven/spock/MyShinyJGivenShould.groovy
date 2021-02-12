package com.tngtech.jgiven.spock

import com.tngtech.jgiven.annotation.ExtendedDescription
import com.tngtech.jgiven.spock.stages.Given
import com.tngtech.jgiven.spock.stages.Then
import com.tngtech.jgiven.spock.stages.When
import spock.lang.Unroll

class MyShinyJGivenShould extends ScenarioSpec<Given, When, Then> {

    def "something should happen"() {
        expect:

        given().some_state()
        when().some_action()
        then().some_outcome()

        assert getScenario().getScenarioModel().getDescription() == "something should happen"
    }

    def "be able to use params"() {
        expect:

        given().some_state_$("param")
        when().some_action()
        then().some_outcome()

        assert getScenario().getScenarioModel().getScenarioCases().get(0).getStep(0).words.join(" ") == "Given some state param"
    }

    @ExtendedDescription("more details")
    def "be able to have extended descriptions"() {
        expect:

        given().some_state()
        when().some_action()
        then().some_outcome()

        assert getScenario().getScenarioModel().getExtendedDescription() == "more details"
    }

    @Unroll
    def "be able to use tables #param"() {
        expect:
        given().some_state_$(param)
        when().some_action()
        then().some_outcome()

        assert getScenario().getScenarioModel().getDescription() == "be able to use tables #param"

        where:
        param        | _
        "param"      | _
        "word param" | _
    }
}

