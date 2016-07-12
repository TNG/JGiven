package com.tngtech.jgiven.annotation;

import java.lang.reflect.Method;

/**
 * Provides a representation of a stage method, scenario or scenario class.
 * @since 0.12.0
 */
public interface AsProvider {

    /**
     * Provide the representation for a stage method or scenario.
     * @param annotation The {@link As} annotation using this provider.
     * @param method The method of which a representation is requested.
     * @return A representation of the method.
     *
     * @since 0.12.0
     */
    String as( As annotation, Method method );

    /**
     * Provide the representation for a scenario class.
     * @param annotation The {@link As} annotation using this provider.
     * @param scenarioClass The scenario class of which a representation is requested.
     * @return A representation of the scenario class.
     *
     * @since 0.12.0
     */
    String as( As annotation, Class<?> scenarioClass );

}
