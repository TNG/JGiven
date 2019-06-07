package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.impl.Scenario;
import org.junit.ClassRule;
import org.junit.Rule;

import com.tngtech.jgiven.base.SimpleScenarioTestBase;

public class SimpleScenarioTest<STEPS> extends SimpleScenarioTestBase<STEPS> {
    @ClassRule
    public static final JGivenClassRule writerRule = new JGivenClassRule();

    @Rule
    public final JGivenMethodRule scenarioRule = new JGivenMethodRule( createScenario() );

    @Override
    @SuppressWarnings("unchecked")
    public Scenario<STEPS, STEPS, STEPS> getScenario() {
        return (Scenario<STEPS, STEPS, STEPS>) scenarioRule.getScenario();
    }
}
