package com.tngtech.jgiven;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;

public class ScenarioTestBaseForTesting<GIVEN, WHEN, THEN> extends ScenarioTestBase<GIVEN,WHEN,THEN> {
    private Scenario<GIVEN, WHEN, THEN> scenario = createScenario();

    @Override
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        return scenario;
    }


}
