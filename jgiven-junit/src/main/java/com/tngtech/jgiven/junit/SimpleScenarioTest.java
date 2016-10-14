package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.impl.Scenario;
import org.junit.ClassRule;
import org.junit.Rule;

import com.tngtech.jgiven.base.SimpleScenarioTestBase;

public class SimpleScenarioTest<STEPS> extends SimpleScenarioTestBase<STEPS> {
    @ClassRule
    public static final ScenarioReportRule writerRule = new ScenarioReportRule();

    @Rule
    public final ScenarioExecutionRule scenarioRule = new ScenarioExecutionRule( createScenario() );

    @Override
    public Scenario<STEPS, STEPS, STEPS> getScenario() {
        return (Scenario<STEPS, STEPS, STEPS>) scenarioRule.getScenario();
    }
}
