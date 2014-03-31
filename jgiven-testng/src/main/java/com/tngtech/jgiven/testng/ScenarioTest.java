package com.tngtech.jgiven.testng;

import org.testng.annotations.Listeners;

import com.tngtech.jgiven.base.ScenarioTestBase;

@Listeners( ScenarioTestListener.class )
public class ScenarioTest<GIVEN, WHEN, THEN> extends ScenarioTestBase<GIVEN, WHEN, THEN> {

}
