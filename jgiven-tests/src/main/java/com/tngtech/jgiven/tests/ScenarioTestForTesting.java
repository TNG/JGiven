package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.impl.ScenarioHolder;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JGivenReportExtractingExtension.class)
public class ScenarioTestForTesting<GIVEN, WHEN, THEN> extends ScenarioTest<GIVEN,WHEN,THEN> {
    @Override
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        if ( ScenarioHolder.get().getScenarioOfCurrentThread() != null) {
            return (Scenario<GIVEN, WHEN, THEN>) ScenarioHolder.get().getScenarioOfCurrentThread();
        }
        return super.getScenario();
    }
}
