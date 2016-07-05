package com.tngtech.jgiven;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.ScenarioState;

/**
 * This interface can be injected into a stage using the {@link ScenarioState} annotation.
 * It provided programmatic access to the current scenario.
 *
 * @since 0.12.0
 */
public interface CurrentScenario {

    /**
     * Dynamically add a tag to the scenario.
     * @param annotationClass The tag annotation class to use.
     * @param values List of custom values.
     * @since 0.12.0
     */
    void addTag( Class<? extends Annotation> annotationClass, String... values );

}
