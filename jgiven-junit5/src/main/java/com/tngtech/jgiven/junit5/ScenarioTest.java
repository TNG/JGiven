package com.tngtech.jgiven.junit5;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;
import org.junit.jupiter.api.extension.ExtendWith;


/**
 * Convenience test base class for writing JGiven scenarios with JUnit 5.
 * If you only have one stage class you can also use the {@link SimpleScenarioTest} class.
 * If you don't want to inherit from any class you can just use the {@link JGivenExtension}
 * directly.
 *
 * @param <GIVEN> the GIVEN stage
 * @param <WHEN> the WHEN stage
 * @param <THEN> the THEN stage
 *
 * @see JGivenExtension
 * @see SimpleScenarioTest
 * @since 0.14.0
 *
 */
@ExtendWith( JGivenExtension.class )
public class ScenarioTest<GIVEN, WHEN, THEN> extends ScenarioTestBase<GIVEN, WHEN, THEN> {

    private Scenario<GIVEN, WHEN, THEN> scenario = createScenario();

    @Override
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        return scenario;
    }
}
