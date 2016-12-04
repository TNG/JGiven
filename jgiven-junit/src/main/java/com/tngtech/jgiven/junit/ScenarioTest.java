package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.impl.Scenario;
import org.junit.ClassRule;
import org.junit.Rule;

import com.tngtech.jgiven.base.ScenarioTestBase;

public class ScenarioTest<GIVEN, WHEN, THEN> extends ScenarioTestBase<GIVEN, WHEN, THEN> {
    @ClassRule
    public static final JGivenClassRule writerRule = new JGivenClassRule();

    @Rule
    public final JGivenMethodRule scenarioRule = new JGivenMethodRule( createScenario() );

    @Override
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        return (Scenario<GIVEN, WHEN, THEN>) scenarioRule.getScenario();
    }
}
