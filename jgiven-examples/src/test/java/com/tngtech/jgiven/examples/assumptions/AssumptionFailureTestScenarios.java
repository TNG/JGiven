package com.tngtech.jgiven.examples.assumptions;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit.JGivenClassRule;
import com.tngtech.jgiven.junit.JGivenMethodRule;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

@SuppressWarnings("NewClassNamingConvention")
public class AssumptionFailureTestScenarios {

    @ClassRule
    public static final JGivenClassRule writerRule = new JGivenClassRule();

    @Rule
    public final JGivenMethodRule scenarioRule = new JGivenMethodRule();

    @ScenarioStage
    AssumptionFailureTestStage givenTestStage;

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void test_with_failing_assumption() {
        Assume.assumeFalse(true);
    }

    @Test
    public void test_with_failing_assumption_in_stage() {
        givenTestStage.given().a_failed_junit_assumption().and()
                .nothing();

    }

    @Test
    public void test_with_failing_assumption_in_second_stage(){
        givenTestStage.given().nothing().and()
                .a_failed_junit_assumption();
    }

    @Test
    public void test_with_failing_assumption_in_a_nested_stage(){
        givenTestStage.given().nothing().a_failed_nested_step().and().nothing();
    }
}
