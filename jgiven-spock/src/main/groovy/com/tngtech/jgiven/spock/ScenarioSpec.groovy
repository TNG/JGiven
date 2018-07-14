package com.tngtech.jgiven.spock

import com.google.common.reflect.TypeToken
import com.tngtech.jgiven.impl.Scenario
import com.tngtech.jgiven.junit.JGivenClassRule
import com.tngtech.jgiven.spock.junit.JGivenSpockMethodRule
import org.junit.ClassRule
import org.junit.Rule
import spock.lang.Shared
import spock.lang.Specification

class ScenarioSpec<GIVEN, WHEN, THEN> extends Specification {

    @ClassRule @Shared JGivenClassRule writerRule = new JGivenClassRule()
    @Rule JGivenSpockMethodRule scenarioRule = new JGivenSpockMethodRule(createScenario())

    GIVEN given() {
        getScenario().given()
    }

    WHEN when() {
        getScenario().when()
    }

    THEN then() {
        getScenario().then()
    }

    Scenario<GIVEN, WHEN, THEN> getScenario() {
        (Scenario<GIVEN, WHEN, THEN>) scenarioRule.getScenario()
    }

    Scenario<GIVEN, WHEN, THEN> createScenario() {
        Class<GIVEN> givenClass = (Class<GIVEN>) new TypeToken<GIVEN>(getClass()) {}.getRawType()
        Class<WHEN> whenClass = (Class<WHEN>) new TypeToken<WHEN>(getClass()) {}.getRawType()
        Class<THEN> thenClass = (Class<THEN>) new TypeToken<THEN>(getClass()) {}.getRawType()

        new Scenario<GIVEN, WHEN, THEN>(givenClass, whenClass, thenClass)
    }
}
