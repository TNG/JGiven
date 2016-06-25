package com.tngtech.jgiven.impl;

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
    private final Class<GIVEN> givenClass;
    private final Class<WHEN> whenClass;
    private final Class<THEN> thenClass;

    private Scenario( Class<GIVEN> stageClass ) {
        this.givenClass = stageClass;
        this.whenClass = null;
        this.thenClass = null;
    }

    public Scenario( Class<GIVEN> givenClass, Class<WHEN> whenClass, Class<THEN> thenClass ) {
        this.givenClass = givenClass;
        this.whenClass = whenClass;
        this.thenClass = thenClass;
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
     * To share state between the different steps instances use the
     * {@link com.tngtech.jgiven.annotation.ScenarioState} annotation
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
     * so no {@link com.tngtech.jgiven.annotation.ScenarioState} annotations are needed
     * to share state between the different steps instances.
     *
     * @param stepsClass the class to use for given, when and then steps
     * @return the new scenario
     */
    public static <STEPS> Scenario<STEPS, STEPS, STEPS> create( Class<STEPS> stepsClass ) {
        return new Scenario<STEPS, STEPS, STEPS>( stepsClass );
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

    @Override
    @SuppressWarnings("unchecked")
    protected void initialize() {
        super.initialize();
        if (whenClass == null) {
            givenStage = (GIVEN) executor.addStage( givenClass );
            whenStage = (WHEN) givenStage;
            thenStage = (THEN) givenStage;
        } else {
            givenStage = executor.addStage( givenClass );
            whenStage = executor.addStage( whenClass );
            thenStage = executor.addStage( thenClass );
        }
    }

    /**
     * Alias for {@link #startScenario(String)}.
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

    public WHEN when( String translatedWhen ) {
        addIntroWord( translatedWhen );
        return getWhenStage();
    }

    public THEN then( String translatedThen ) {
        addIntroWord( translatedThen );
        return getThenStage();
    }

}
