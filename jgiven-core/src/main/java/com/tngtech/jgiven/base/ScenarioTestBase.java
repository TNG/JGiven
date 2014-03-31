package com.tngtech.jgiven.base;

import com.google.common.reflect.TypeToken;
import com.tngtech.jgiven.Scenario;
import com.tngtech.jgiven.integration.CanWire;

/**
 * Base class for Scenario tests.
 * This class is typically not directly used by end users,
 * but instead test-framework-specific classes for JUnit or TestNG
 */
public class ScenarioTestBase<GIVEN, WHEN, THEN> {

    protected Scenario<GIVEN, WHEN, THEN> scenario = createScenario();

    @SuppressWarnings( { "serial", "unchecked" } )
    protected Scenario<GIVEN, WHEN, THEN> createScenario() {
        Class<GIVEN> givenClass = (Class<GIVEN>) new TypeToken<GIVEN>( getClass() ) {}.getRawType();
        Class<WHEN> whenClass = (Class<WHEN>) new TypeToken<WHEN>( getClass() ) {}.getRawType();
        Class<THEN> thenClass = (Class<THEN>) new TypeToken<THEN>( getClass() ) {}.getRawType();

        return new Scenario<GIVEN, WHEN, THEN>( givenClass, whenClass, thenClass );
    }

    public GIVEN given() {
        return scenario.given();
    }

    public WHEN when() {
        return scenario.when();
    }

    public THEN then() {
        return scenario.then();
    }

    public void wireSteps( CanWire canWire ) {
        scenario.wireSteps( canWire );
    }

    /**
     * Add a new step definition class to the scenario.
     * @param stepsClass the class with the step definitions
     * @return a new instance of the given class
     */
    public <T> T addSteps( Class<T> stepsClass ) {
        return scenario.addSteps( stepsClass );
    }

    /**
     * @return the scenario associated with this test
     */
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        return scenario;
    }

    /**
     * Creates a new scenario for this test
     * @return the new scenario
     */
    public Scenario<GIVEN, WHEN, THEN> createNewScenario() {
        scenario = createScenario();
        return scenario;
    }

}
