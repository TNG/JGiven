package com.tngtech.jgiven.testng;

import com.tngtech.jgiven.base.DualScenarioTestBase;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.impl.ScenarioHolder;
import org.testng.annotations.Listeners;

@Listeners( ScenarioTestListener.class )
public class DualScenarioTest<GIVEN_WHEN, THEN> extends DualScenarioTestBase<GIVEN_WHEN, THEN> {

    @Override
    public Scenario<GIVEN_WHEN, GIVEN_WHEN, THEN> getScenario() {
        return (Scenario<GIVEN_WHEN, GIVEN_WHEN, THEN>) ScenarioHolder.get().getScenarioOfCurrentThread();
    }
}
