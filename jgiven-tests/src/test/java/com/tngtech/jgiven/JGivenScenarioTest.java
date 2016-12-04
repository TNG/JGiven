package com.tngtech.jgiven;

import com.tngtech.jgiven.impl.Scenario;
import org.junit.ClassRule;
import org.junit.Rule;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.junit.JGivenMethodRule;
import com.tngtech.jgiven.junit.JGivenClassRule;

/**
 * We do not directly inherit from ScenarioTest to avoid interference with JGiven tests we are testing
 */
@JGivenConfiguration( JGivenTestConfiguration.class )
public class JGivenScenarioTest<GIVEN, WHEN, THEN> extends ScenarioTestBase<GIVEN, WHEN, THEN> {
    @ClassRule
    public static final JGivenClassRule writerRule = new JGivenClassRule();

    @Rule
    public final JGivenMethodRule scenarioRule = new JGivenMethodRule( createScenario() );

    @Override
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        return (Scenario<GIVEN, WHEN, THEN>) scenarioRule.getScenario();
    }
}
