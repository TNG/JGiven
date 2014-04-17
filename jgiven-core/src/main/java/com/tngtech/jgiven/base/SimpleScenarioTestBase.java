package com.tngtech.jgiven.base;

import com.google.common.reflect.TypeToken;
import com.tngtech.jgiven.impl.Scenario;

/**
 * ScenarioTest that only takes a single type parameter that is
 * used for all three step definition types. 
 * Useful for simple scenarios where multiple classes for step definitions offer no benefit.
 * This class is typically not directly used by end users,
 * but instead test-framework-specific classes for JUnit or TestNG
 */
public class SimpleScenarioTestBase<STEPS> extends ScenarioTestBase<STEPS, STEPS, STEPS> {

    @Override
    @SuppressWarnings( { "serial", "unchecked" } )
    protected Scenario<STEPS, STEPS, STEPS> createScenario() {
        Class<STEPS> stepsClass = (Class<STEPS>) new TypeToken<STEPS>( getClass() ) {}.getRawType();
        return Scenario.create( stepsClass );
    }

}
