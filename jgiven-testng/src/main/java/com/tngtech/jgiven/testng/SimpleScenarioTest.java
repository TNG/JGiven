package com.tngtech.jgiven.testng;

import org.testng.annotations.Listeners;

import com.tngtech.jgiven.base.SimpleScenarioTestBase;

/**
 * Base class for TestNG-based scenario tests that only have a single class with step definitions.
 * 
 * @param <STEPS> a class that contains the step definitions, typically inheriting from {@link com.tngtech.jgiven.Stage}
 */
@Listeners( ScenarioTestListener.class )
public class SimpleScenarioTest<STEPS> extends SimpleScenarioTestBase<STEPS> {

}
