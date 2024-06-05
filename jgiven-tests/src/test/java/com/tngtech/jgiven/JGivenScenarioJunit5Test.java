package com.tngtech.jgiven;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.junit5.JGivenExtension;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * We do not directly inherit from ScenarioTest to avoid interference with JGiven tests we are testing
 */
@JGivenConfiguration( JGivenTestConfiguration.class )
@ExtendWith( JGivenExtension.class )
public class JGivenScenarioJunit5Test<GIVEN, WHEN, THEN> extends ScenarioTestBase<GIVEN, WHEN, THEN> {

    private Scenario<GIVEN, WHEN, THEN> scenario = createScenario();

    @Override
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        return scenario;
    }
}