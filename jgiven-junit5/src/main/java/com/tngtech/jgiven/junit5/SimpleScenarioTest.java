package com.tngtech.jgiven.junit5;

import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.jgiven.base.SimpleScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;

/**
 * JUnit 5
 *
 * @since 0.14.0
*/
@ExtendWith( JGivenExtension.class )
public class SimpleScenarioTest<STAGE> extends SimpleScenarioTestBase<STAGE> {

    private Scenario<STAGE, STAGE, STAGE> scenario = createScenario();

    @Override
    public Scenario<STAGE, STAGE, STAGE> getScenario() {
        return scenario;
    }
}
