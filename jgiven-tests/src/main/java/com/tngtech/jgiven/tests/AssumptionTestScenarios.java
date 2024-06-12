package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.testng.ScenarioTestListener;
import org.junit.Assume;
import org.junit.Test;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testng.SkipException;
import org.testng.annotations.Listeners;

@SuppressWarnings({"NewClassNamingConvention", "JUnit3StyleTestMethodInJUnit4Class", "DataFlowIssue", "JUnitMixedFramework"})
@Listeners(ScenarioTestListener.class)
@ExtendWith(JGivenReportExtractingExtension.class)
public class AssumptionTestScenarios extends ScenarioTestForTesting<GivenTestStage, WhenTestStage, ThenTestStage>{


    @org.testng.annotations.Test
    public void test_with_failing_testng_assumption() {
        throw new SkipException("Fail on purpose");
    }

    @org.testng.annotations.Test
    public void test_with_failing_testng_assumption_in_stage() {
        given().a_failed_testng_assumption();
    }

    @org.junit.jupiter.api.Test
    public void test_with_failing_junit5_assumption() {
        Assumptions.assumeFalse(true);
    }


    @org.junit.jupiter.api.Test
    public void test_with_failing_junit5_assumption_in_stage() {
        given().a_failed_junit5_assumption();
    }

    @Test
    public void test_with_failing_junit_assumption() {
        Assume.assumeFalse(true);
    }


    @Test
    public void test_with_failing_junit_assumption_in_stage() {
        given().a_failed_junit_assumption();
    }

}
