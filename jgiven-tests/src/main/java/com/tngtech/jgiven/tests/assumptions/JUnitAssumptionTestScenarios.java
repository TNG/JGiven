package com.tngtech.jgiven.tests.assumptions;

import com.tngtech.jgiven.testng.ScenarioTestListener;
import com.tngtech.jgiven.tests.*;
import org.junit.Assume;
import org.junit.Test;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testng.SkipException;
import org.testng.annotations.Listeners;

import static org.assertj.core.api.Assumptions.assumeThat;

@SuppressWarnings({"NewClassNamingConvention", "JUnit3StyleTestMethodInJUnit4Class", "DataFlowIssue", "JUnitMixedFramework"})
public class JUnitAssumptionTestScenarios extends ScenarioTestForTesting<GivenTestStage, WhenTestStage, ThenTestStage> {

    @Test
    public void test_with_failing_junit_assumption() {
        Assume.assumeFalse(true);
    }

    @Test
    public void test_with_failing_junit_assumption_in_stage() {
        given().a_failed_junit_assumption();
    }
}
