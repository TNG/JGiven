package com.tngtech.jgiven.spock

import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.spock.stages.GivenSomeState
import com.tngtech.jgiven.spock.stages.ThenSomeOutcome
import com.tngtech.jgiven.spock.stages.WhenSomeAction
import spock.lang.Unroll

@As('My shiny JGiven should')
class MyShinyJGivenShould extends ScenarioSpec<GivenSomeState, WhenSomeAction, ThenSomeOutcome> {

    def "work with Spock"() {
        expect:

        given().some_state()
        when().some_action()
        then().some_outcome()
    }

    @Unroll
    def "be able to use tables #param"() {
        expect:
        given().some_state_$(param)
        when().some_action()
        then().some_outcome()

        where:
        param        | _
        "param"      | _
        "word param" | _
    }
}
