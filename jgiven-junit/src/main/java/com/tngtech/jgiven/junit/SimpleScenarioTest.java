package com.tngtech.jgiven.junit;

import org.junit.ClassRule;
import org.junit.Rule;

import com.tngtech.jgiven.base.SimpleScenarioTestBase;

public class SimpleScenarioTest<STEPS> extends SimpleScenarioTestBase<STEPS> {
    @ClassRule
    public static final ScenarioReportRule writerRule = new ScenarioReportRule();

    @Rule
    public final ScenarioExecutionRule scenarioRule = new ScenarioExecutionRule( writerRule, this, getScenario() );

}
