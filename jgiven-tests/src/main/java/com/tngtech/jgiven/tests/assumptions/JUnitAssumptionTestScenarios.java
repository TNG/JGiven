package com.tngtech.jgiven.tests.assumptions;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.junit.JGivenClassRule;
import com.tngtech.jgiven.junit.JGivenMethodRule;
import com.tngtech.jgiven.tests.GivenTestStage;
import com.tngtech.jgiven.tests.ScenarioTestForTesting;
import com.tngtech.jgiven.tests.ThenTestStage;
import com.tngtech.jgiven.tests.WhenTestStage;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

@SuppressWarnings("NewClassNamingConvention")
public class JUnitAssumptionTestScenarios {

    @ClassRule
    public static final JGivenClassRule writerRule = new JGivenClassRule();

    @Rule
    public final JGivenMethodRule scenarioRule = new JGivenMethodRule();

    @ScenarioStage
    GivenTestStage givenTestStage;

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void test_with_failing_assumption() {
        Assume.assumeFalse(true);
    }

    @Test
    public void test_with_failing_assumption_in_stage() {
        givenTestStage.given().a_failed_junit_assumption();
    }

    @Test
    public void test_with_failing_assumption_in_second_stage(){
        givenTestStage.given().nothing()
                .a_failed_junit_assumption();
    }
}
