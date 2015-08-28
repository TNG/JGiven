package com.tngtech.jgiven.testng;

import org.testng.annotations.Listeners;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;

@Listeners( ScenarioTestListener.class )
public class ScenarioTest<GIVEN, WHEN, THEN> extends ScenarioTestBase<GIVEN, WHEN, THEN> {

    private static final ThreadLocal<Scenario<?, ?, ?>> scenarioOfThread = new ThreadLocal<Scenario<?, ?, ?>>();

    @Override
    @SuppressWarnings( { "serial", "unchecked" } )
    protected Scenario<GIVEN, WHEN, THEN> createScenario() {
        Scenario<GIVEN, WHEN, THEN> scenario = super.createScenario();
        scenarioOfThread.set( scenario );
        return scenario;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        return (Scenario<GIVEN, WHEN, THEN>) scenarioOfThread.get();
    }
}
