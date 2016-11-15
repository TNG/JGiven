package com.tngtech.jgiven.integration.spring;

import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.junit.ScenarioExecutionRule;
import com.tngtech.jgiven.junit.ScenarioReportRule;
import org.junit.ClassRule;
import org.junit.Rule;

/**
 * A variant of {@link SpringRuleScenarioTest} works with a single
 * stage type parameter instead of three.
 *
 * @param <STEPS> the stage class that contains the step definitions
 *
 * @since 0.14.0
 */
public class SimpleSpringRuleScenarioTest<STEPS> extends InternalSimpleSpringScenarioTest<STEPS> {

    @ClassRule
    public static final ScenarioReportRule writerRule = new ScenarioReportRule();

    @Rule
    public final ScenarioExecutionRule scenarioRule = new ScenarioExecutionRule( createScenario() );

    @Override
    public Scenario<STEPS, STEPS, STEPS> getScenario() {
        return (Scenario<STEPS, STEPS, STEPS>) scenarioRule.getScenario();
    }
}
