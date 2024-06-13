package com.tngtech.jgiven.tests.assumptions;

import com.tngtech.jgiven.tests.GivenTestStage;
import com.tngtech.jgiven.tests.ScenarioTestForTesting;
import com.tngtech.jgiven.tests.ThenTestStage;
import com.tngtech.jgiven.tests.WhenTestStage;
import org.junit.Assume;
import org.junit.Test;

@SuppressWarnings("NewClassNamingConvention")
public class JUnitAssumptionTestScenarios extends ScenarioTestForTesting<GivenTestStage, WhenTestStage, ThenTestStage> {

    @SuppressWarnings("DataFlowIssue")
    @Test
    public void test_with_failing_assumption() {
        Assume.assumeFalse(true);
    }

    @Test
    public void test_with_failing_assumption_in_stage() {
        given().a_failed_junit_assumption();
    }

    @Test
    public void test_with_failing_assumption_in_second_stage(){
        given().nothing()
                .a_failed_junit_assumption();
    }
}
