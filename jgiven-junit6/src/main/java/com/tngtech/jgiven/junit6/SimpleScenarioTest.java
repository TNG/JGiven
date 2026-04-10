package com.tngtech.jgiven.junit6;

import com.tngtech.jgiven.base.SimpleScenarioTestBase;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.jgiven.impl.Scenario;


/**
 * Convenience test base class for writing JGiven scenarios with JUnit 5 and JUnit 6 using a single stage class.
 *
 * @param <STAGE> the stage class
 *
 * @see JGivenExtension
 * @see ScenarioTest
 */
@ExtendWith( JGivenExtension.class )
public class SimpleScenarioTest<STAGE> extends SimpleScenarioTestBase<STAGE> {

    private Scenario<STAGE, STAGE, STAGE> scenario = createScenario();

    @Override
    public Scenario<STAGE, STAGE, STAGE> getScenario() {
        return scenario;
    }
}
