package com.tngtech.jgiven.integration.spring;

import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.junit.JGivenClassRule;
import com.tngtech.jgiven.junit.JGivenMethodRule;
import org.junit.ClassRule;
import org.junit.Rule;

/**
 * A variant of {@link SpringRuleScenarioTest} works with two
 * stage type parameters instead of three.
 *
 * @param <GIVEN_WHEN> the stage class that contains the step definitions for given and when
 * @param <THEN> the stage class that contains the step definitions for then
 *
 *
 * @since 0.13.0
 */
public class DualSpringRuleScenarioTest<GIVEN_WHEN, THEN> extends
    InternalDualSpringScenarioTest<GIVEN_WHEN,THEN> {

    @ClassRule
    public static final JGivenClassRule writerRule = new JGivenClassRule();

    @Rule
    public final JGivenMethodRule scenarioRule = new JGivenMethodRule( createScenario() );

    @Override
    public Scenario<GIVEN_WHEN, GIVEN_WHEN, THEN> getScenario() {
        return (Scenario<GIVEN_WHEN, GIVEN_WHEN, THEN>) scenarioRule.getScenario();
    }
}
