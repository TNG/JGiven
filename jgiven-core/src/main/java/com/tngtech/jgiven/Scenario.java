package com.tngtech.jgiven;

import com.google.common.reflect.TypeToken;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.base.ScenarioBase;

/**
 * A default scenario implementation that takes three type arguments,
 * one for each stage.
 * 
 * @param <GIVEN> the Given stage
 * @param <WHEN> then When stage
 * @param <THEN> then Then stage
 */
public class Scenario<GIVEN, WHEN, THEN> extends ScenarioBase {

    private GIVEN givenStage;
    private WHEN whenStage;
    private THEN thenStage;

    @SuppressWarnings( { "serial", "unchecked" } )
    public Scenario() {
        Class<GIVEN> givenClass = (Class<GIVEN>) new TypeToken<GIVEN>( getClass() ) {}.getRawType();
        Class<WHEN> whenClass = (Class<WHEN>) new TypeToken<WHEN>( getClass() ) {}.getRawType();
        Class<THEN> thenClass = (Class<THEN>) new TypeToken<THEN>( getClass() ) {}.getRawType();
        setupStages( givenClass, whenClass, thenClass );
    }

    @SuppressWarnings( "unchecked" )
    private Scenario( Class<?> stageClass ) {
        givenStage = (GIVEN) executor.addStage( stageClass );
        whenStage = (WHEN) givenStage;
        thenStage = (THEN) givenStage;
    }

    public Scenario( Class<GIVEN> givenClass, Class<WHEN> whenClass, Class<THEN> thenClass ) {
        setupStages( givenClass, whenClass, thenClass );
    }

    private void setupStages( Class<GIVEN> givenClass, Class<WHEN> whenClass, Class<THEN> thenClass ) {
        givenStage = executor.addStage( givenClass );
        whenStage = executor.addStage( whenClass );
        thenStage = executor.addStage( thenClass );
    }

    public GIVEN getGivenStage() {
        return givenStage;
    }

    public WHEN getWhenStage() {
        return whenStage;
    }

    public THEN getThenStage() {
        return thenStage;
    }

    public void addIntroWord( String word ) {
        executor.addIntroWord( word );
    }

    /**
     * Creates a scenario with 3 different steps classes.
     * 
     * To share state between the different steps instances use the {@link ScenarioState} annotation
     * 
     * @param givenClass the Given steps class
     * @param whenClass the When steps class
     * @param thenClass the Then steps class
     * @return the new scenario
     */
    public static <GIVEN, WHEN, THEN> Scenario<GIVEN, WHEN, THEN> create( Class<GIVEN> givenClass, Class<WHEN> whenClass,
            Class<THEN> thenClass ) {
        return new Scenario<GIVEN, WHEN, THEN>( givenClass, whenClass, thenClass );
    }

    /**
     * Creates a scenario with a single steps class.
     * Only creates a single steps instance for all three step types, 
     * so no {@link ScenarioState} annotations are needed to share state between the different steps instances.
     * 
     * @param stepsClass the class to use for given, when and then steps
     * @return the new scenario
     */
    public static <STEPS> Scenario<STEPS, STEPS, STEPS> create( Class<STEPS> stepsClass ) {
        Scenario<STEPS, STEPS, STEPS> scenario = new Scenario<STEPS, STEPS, STEPS>( stepsClass );
        return scenario;
    }

    /**
     * Describes the scenario. Must be called before any step invocation.
     * @param description the description
     * @return this for a fluent interface
     */
    @Override
    public Scenario<GIVEN, WHEN, THEN> startScenario( String description ) {
        super.startScenario( description );
        return this;

    }

    /**
     * Alias for {@link #startScenario(String)}
     */
    public Scenario<GIVEN, WHEN, THEN> as( String description ) {
        return startScenario( description );
    }

    public GIVEN given() {
        return given( "Given" );
    }

    public WHEN when() {
        return when( "When" );
    }

    public THEN then() {
        return then( "Then" );
    }

    public GIVEN given( String translatedGiven ) {
        addIntroWord( translatedGiven );
        return getGivenStage();
    }

    public WHEN when( String translatedGiven ) {
        addIntroWord( translatedGiven );
        return getWhenStage();
    }

    public THEN then( String translatedGiven ) {
        addIntroWord( translatedGiven );
        return getThenStage();
    }
}
