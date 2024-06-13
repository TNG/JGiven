package com.tngtech.jgiven.tests.assumptions;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.testng.ScenarioTestListener;
import com.tngtech.jgiven.tests.GivenTestStage;
import com.tngtech.jgiven.tests.ScenarioTestForTesting;
import com.tngtech.jgiven.tests.ThenTestStage;
import com.tngtech.jgiven.tests.WhenTestStage;
import org.testng.SkipException;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(ScenarioTestListener.class)
public class TestNgAssumptionTestScenarios {

    @ScenarioStage
    GivenTestStage givenTestStage;

    @Test
    public void test_with_failing_assumption() {
        throw new SkipException("Fail on purpose");
    }

    @Test
    public void test_with_failing_assumption_in_second_stage() {
        givenTestStage.given().nothing()
                .a_failed_testng_assumption();
    }

    @Test
    public void test_with_failing_assumption_in_stage() {
        givenTestStage.given().a_failed_testng_assumption();
    }
}
