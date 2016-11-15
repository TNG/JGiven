package com.tngtech.jgiven.integration.spring;

import org.junit.ClassRule;
import org.junit.Rule;

import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.junit.ScenarioExecutionRule;
import com.tngtech.jgiven.junit.ScenarioReportRule;

/**
 * Base class for {@link SpringScenarioExecutor} based JGiven tests
 *
 * Uses JUnit rules (introduced in Spring 4.2) instead of a JUnit runner in
 * order to allow custom JUnit runners.
 *
 *
 * @param <GIVEN>
 * @param <WHEN>
 * @param <THEN>
 *
 * @since 0.14.0
 */
public class SpringRuleScenarioTest<GIVEN, WHEN, THEN> extends InternalSpringScenarioTest<GIVEN, WHEN, THEN> {

    @ClassRule
    public static final ScenarioReportRule writerRule = new ScenarioReportRule();

    @Rule
    public final ScenarioExecutionRule scenarioRule = new ScenarioExecutionRule( createScenario() );

    @Override
    public Scenario<GIVEN, WHEN, THEN> getScenario() {
        return (Scenario<GIVEN, WHEN, THEN>) scenarioRule.getScenario();
    }
}
