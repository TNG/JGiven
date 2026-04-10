package com.tngtech.jgiven.junit5;

import com.tngtech.jgiven.base.DualScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;
import org.junit.jupiter.api.extension.ExtendWith;


/**
 * Convenience test base class for writing JGiven scenarios with JUnit 5.
 * If you only have one stage class you can also use the {@link SimpleScenarioTest} class.
 * If you don't want to inherit from any class you can just use the {@link JGivenExtension}
 * directly.
 *
 * @param <GIVEN_WHEN> the combined GIVEN and WHEN stage
 * @param <THEN> the THEN stage
 *
 * @see JGivenExtension
 * @see SimpleScenarioTest
 * @deprecated As of JGiven 3.0.0, the junit5 module is deprecated in favor of {@code jgiven-junit6}.
 *             The junit6 module supports both JUnit 5 and JUnit 6, and provides forward compatibility.
 *             This module will continue to work with JUnit 5, but new projects should use junit6.
 */
@Deprecated(since = "3.0.0", forRemoval = false)
@ExtendWith( JGivenExtension.class )
public class DualScenarioTest<GIVEN_WHEN, THEN> extends DualScenarioTestBase<GIVEN_WHEN, THEN> {

    private Scenario<GIVEN_WHEN,GIVEN_WHEN, THEN> scenario = createScenario();

    @Override
    public Scenario<GIVEN_WHEN, GIVEN_WHEN, THEN> getScenario() {
        return scenario;
    }
}
