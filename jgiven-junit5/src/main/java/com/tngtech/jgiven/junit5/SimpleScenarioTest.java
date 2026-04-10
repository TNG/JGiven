package com.tngtech.jgiven.junit5;

import com.tngtech.jgiven.base.SimpleScenarioTestBase;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.jgiven.impl.Scenario;


/**
 * Convenience test base class for writing JGiven scenarios with JUnit 5 using a single stage class.
 *
 * @param <STAGE> the stage class
 *
 * @see JGivenExtension
 * @see ScenarioTest
 * @deprecated As of JGiven 3.0.0, the junit5 module is deprecated in favor of {@code jgiven-junit6}.
 *             The junit6 module supports both JUnit 5 and JUnit 6, and provides forward compatibility.
 *             This module will continue to work with JUnit 5, but new projects should use junit6.
 */
@Deprecated(since = "3.0.0", forRemoval = false)
@ExtendWith( JGivenExtension.class )
public class SimpleScenarioTest<STAGE> extends SimpleScenarioTestBase<STAGE> {

    private Scenario<STAGE, STAGE, STAGE> scenario = createScenario();

    @Override
    public Scenario<STAGE, STAGE, STAGE> getScenario() {
        return scenario;
    }
}
