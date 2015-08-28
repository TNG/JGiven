package com.tngtech.jgiven.junit;

import org.junit.ClassRule;
import org.junit.Rule;

import com.tngtech.jgiven.base.ScenarioTestBase;

public class ScenarioTest<GIVEN, WHEN, THEN> extends ScenarioTestBase<GIVEN, WHEN, THEN> {
    @ClassRule
    public static final ScenarioReportRule writerRule = new ScenarioReportRule();

    @Rule
    public final ScenarioExecutionRule scenarioRule = new ScenarioExecutionRule( getScenario() );

}
