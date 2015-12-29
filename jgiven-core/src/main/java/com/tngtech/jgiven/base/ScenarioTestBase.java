package com.tngtech.jgiven.base;

import com.google.common.reflect.TypeToken;
import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.integration.CanWire;

/**
 * Base class for Scenario tests.
 * This class is typically not directly used by end users,
 * but instead test-framework-specific classes for JUnit or TestNG
 */
public class ScenarioTestBase<GIVEN, WHEN, THEN> {

    private Scenario<GIVEN, WHEN, THEN> scenario = createScenario();

    @SuppressWarnings( { "serial", "unchecked" } )
    protected Scenario<GIVEN, WHEN, THEN> createScenario() {
        Class<GIVEN> givenClass = (Class<GIVEN>) new TypeToken<GIVEN>( getClass() ) {}.getRawType();
        Class<WHEN> whenClass = (Class<WHEN>) new TypeToken<WHEN>( getClass() ) {}.getRawType();
        Class<THEN> thenClass = (Class<THEN>) new TypeToken<THEN>( getClass() ) {}.getRawType();

        return new Scenario<GIVEN, WHEN, THEN>( givenClass, whenClass, thenClass );
    }

    public GIVEN given() {
        return getScenario().given();
    }

    public WHEN when() {
        return getScenario().when();
    }

    public THEN then() {
        return getScenario().then();
    }

    /**
     * Adds a new section to the scenario
     * <h1>EXPERIMENTAL FEATURE</h1>
     * This is an experimental feature. It might change in the future.
     * If you have any feedback regarding this feature, please let us know
     * by creating an issue at https://github.com/TNG/JGiven/issues
     * @param sectionTitle the title of the section
     * @since 0.11.0
     */
    public void section( String sectionTitle ) {
        getScenario().section( sectionTitle );
    }

    public void wireSteps( CanWire canWire ) {
        getScenario().wireSteps( canWire );
    }

    /**
     * Add a new stage class to the scenario.
     * @param stageClass the class with the step definitions
     * @return a new instance of the given class enhanced by JGiven
     */
    public <T> T addStage( Class<T> stageClass ) {
        return getScenario().addStage( stageClass );
    }

    /**
     * @return the scenario associated with this test
     */
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        return scenario;
    }

    /**
     * Creates a new scenario for this test.
     * @return the new scenario
     */
    public Scenario<GIVEN, WHEN, THEN> createNewScenario() {
        this.scenario = createScenario();
        return getScenario();
    }
}
