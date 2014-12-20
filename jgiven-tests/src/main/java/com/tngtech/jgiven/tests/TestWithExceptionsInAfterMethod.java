package com.tngtech.jgiven.tests;

import org.junit.After;
import org.junit.Test;

import com.tngtech.jgiven.junit.ScenarioTest;

public class TestWithExceptionsInAfterMethod extends ScenarioTest<GivenTestStage, WhenTestStage, ThenTestStage> {

    @After
    public void afterException() {
        throw new IllegalStateException( "exception in after method" );
    }

    @Test
    public void test_that_exception_in_scenario_is_not_hidden_by_exception_in_JUnit_after_method() {
        given().nothing();
        when().a_step_fails();
    }

}
