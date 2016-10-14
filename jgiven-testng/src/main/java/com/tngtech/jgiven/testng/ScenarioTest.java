package com.tngtech.jgiven.testng;

import org.testng.annotations.Listeners;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;

@Listeners( ScenarioTestListener.class )
public class ScenarioTest<GIVEN, WHEN, THEN> extends ScenarioTestBase<GIVEN, WHEN, THEN> {

    @Override
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        return (Scenario<GIVEN, WHEN, THEN>) ScenarioHolder.get().getScenarioOfCurrentThread();
    }
}
