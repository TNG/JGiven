package com.tngtech.jgiven;

import org.junit.ClassRule;
import org.junit.Rule;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.junit.ScenarioExecutionRule;
import com.tngtech.jgiven.junit.ScenarioReportRule;

/**
 * We do not directly inherit from ScenarioTest to avoid interference with JGiven tests we are testing
 */
@JGivenConfiguration( JGivenTestConfiguration.class )
public class JGivenScenarioTest<GIVEN, WHEN, THEN> extends ScenarioTestBase<GIVEN, WHEN, THEN> {
    @ClassRule
    public static final ScenarioReportRule writerRule = new ScenarioReportRule();

    @Rule
    public final ScenarioExecutionRule scenarioRule = new ScenarioExecutionRule( writerRule, this, getScenario() );

}
