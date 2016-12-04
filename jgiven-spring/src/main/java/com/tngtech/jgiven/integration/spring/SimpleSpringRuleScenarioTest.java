package com.tngtech.jgiven.integration.spring;

import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.junit.JGivenMethodRule;
import com.tngtech.jgiven.junit.JGivenClassRule;
import org.junit.ClassRule;
import org.junit.Rule;

/**
 * A variant of {@link SpringRuleScenarioTest} works with a single
 * stage type parameter instead of three.
 *
 * @param <STEPS> the stage class that contains the step definitions
 *
 * @since 0.13.0
 */
public class SimpleSpringRuleScenarioTest<STEPS> extends InternalSimpleSpringScenarioTest<STEPS> {

    @ClassRule
    public static final JGivenClassRule writerRule = new JGivenClassRule();

    @Rule
    public final JGivenMethodRule scenarioRule = new JGivenMethodRule( createScenario() );

    @Override
    public Scenario<STEPS, STEPS, STEPS> getScenario() {
        return (Scenario<STEPS, STEPS, STEPS>) scenarioRule.getScenario();
    }
}
