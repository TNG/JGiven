package com.tngtech.jgiven.tests;

import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JGivenReportExtractingExtension.class)
public class TestWithExceptionsInAfterMethod
    extends ScenarioTestForTesting<GivenTestStage, WhenTestStage, ThenTestStage> {

    @After
    @AfterEach
    public void afterException() {
        throw new IllegalStateException("exception in after method");
    }

    @Test
    @org.junit.jupiter.api.Test
    public void test_that_exception_in_scenario_is_not_hidden_by_exception_in_JUnit_after_method() {
        given().nothing();
        when().a_step_fails();
    }

}
