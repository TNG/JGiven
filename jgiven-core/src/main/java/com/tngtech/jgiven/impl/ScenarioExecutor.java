package com.tngtech.jgiven.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import com.tngtech.jgiven.impl.intercept.ScenarioListener;
import com.tngtech.jgiven.integration.CanWire;
import com.tngtech.jgiven.report.model.NamedArgument;

public interface ScenarioExecutor {

    static final int INITIAL_MAX_STEP_DEPTH = 1;

    public enum State {
        INIT,
        STARTED,
        FINISHED
    }

    <T> T addStage( Class<T> stepsClass );

    <T> T createStageClass( Class<T> stepsClass );

    void addIntroWord( String word );

    void addSection( String sectionTitle );

    void readScenarioState( Object object );

    /**
     * Used for DI frameworks to inject values into stages.
     */
    void wireSteps( CanWire canWire );

    /**
     * Has to be called when the scenario is finished in order to execute after methods.
     */
    void finished() throws Throwable;

    void injectStages( Object stage );

    boolean hasFailed();

    Throwable getFailedException();

    void setFailedException( Exception e );

    void failed( Throwable e );

    /**
     * Starts a scenario with the given description.
     *
     * @param description the description of the scenario
     */
    void startScenario( String description );

    /**
     * Starts the scenario with the given method and arguments.
     * Derives the description from the method name.
     * @param method the method that started the scenario
     * @param arguments the test arguments with their parameter names
     */
    void startScenario( Class<?> testClass, Method method, List<NamedArgument> arguments );

    void setListener( ScenarioListener listener );

    void failIfPass();

}