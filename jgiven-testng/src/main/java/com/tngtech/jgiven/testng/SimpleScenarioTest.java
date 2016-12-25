package com.tngtech.jgiven.testng;

import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.impl.ScenarioHolder;
import org.testng.annotations.Listeners;

import com.tngtech.jgiven.base.SimpleScenarioTestBase;

/**
 * Base class for TestNG-based scenario tests that only have a single class with step definitions.
 * 
 * @param <STEPS> a class that contains the step definitions, typically inheriting from {@link com.tngtech.jgiven.Stage}
 */
@Listeners( ScenarioTestListener.class )
public class SimpleScenarioTest<STEPS> extends SimpleScenarioTestBase<STEPS> {

    @Override
    public Scenario<STEPS, STEPS, STEPS> getScenario() {
        return (Scenario<STEPS, STEPS, STEPS>) ScenarioHolder.get().getScenarioOfCurrentThread();
    }

}
