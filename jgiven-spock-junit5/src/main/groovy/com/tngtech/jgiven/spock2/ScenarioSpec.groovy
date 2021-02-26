package com.tngtech.jgiven.spock2

import com.google.common.reflect.TypeToken

import org.junit.jupiter.api.extension.ExtendWith;
import com.tngtech.jgiven.junit5.JGivenExtension;
import com.tngtech.jgiven.impl.Scenario;
import spock.lang.Specification

@ExtendWith( JGivenExtension.class )
class ScenarioSpec<GIVEN, WHEN, THEN> extends Specification {

    GIVEN given() {
        return getScenario().given()
    }

    WHEN when() {
        return getScenario().when()
    }

    THEN then() {
        return getScenario().then()
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
