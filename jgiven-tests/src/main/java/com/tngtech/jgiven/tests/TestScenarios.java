package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.testng.ScenarioTestListener;
import org.junit.Test;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testng.SkipException;
import org.testng.annotations.Listeners;

import static org.assertj.core.api.Assumptions.assumeThat;

@Listeners( ScenarioTestListener.class )
@ExtendWith(JGivenReportExtractingExtension.class)
public class TestScenarios extends ScenarioTestForTesting<GivenTestStage, WhenTestStage, ThenTestStage> {

    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    public void test_with_failing_assertJ_assumption() {
        assumeThat(false).isTrue();
    }

    @org.testng.annotations.Test
    public void test_with_failing_testng_assumption() {
        throw new SkipException("Fail on purpose");
    }
    @Test
    @org.junit.jupiter.api.Test
    public void test_with_failing_junit_assumption() {
        Assumptions.assumeFalse(true);
    }

    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    public void test_with_failing_assertJ_assumption_in_stage() {
        given().a_failed_assertJ_assumption();
    }

    @org.testng.annotations.Test
    public void test_with_failing_testng_assumption_in_stage() {
        given().a_failed_testng_assumption();
    }

    @Test
    @org.junit.jupiter.api.Test
    public void test_with_failing_junit_assumption_in_stage() {
        given().a_failed_junit_assumption();
    }


    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    public void failing_test_with_two_steps() {
        given().an_exception_is_thrown();
        when().something_happens();
    }

    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    public void failing_test_with_three_steps() {
        given().an_exception_is_thrown();
        when().something_happens();
        then().something_happened();
    }

    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    public void failing_test_with_two_steps_and_second_step_fails() {
        given().nothing();
        when().a_step_fails();
    }

    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    public void failing_test_with_two_failing_stages() {
        given().an_exception_is_thrown();
        when().a_step_fails();
    }

    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    public void failing_test_where_second_stage_has_a_failing_after_stage_method() {
        FailingAfterStageMethodStage stage = addStage( FailingAfterStageMethodStage.class );
        given().an_exception_is_thrown();
        stage.when().nothing();
    }

    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    @Pending
    public void failing_test_with_Pending_annotation() {
        given().an_exception_is_thrown();
        when().something_happens();
    }

    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    @Pending
    public void passing_test_with_Pending_annotation() {
        given().nothing();
    }

    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    @Pending( failIfPass = true )
    public void passing_test_with_Pending_annotation_and_failIfPassed_set_to_true() {
        given().nothing();
    }

    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    @Pending( failIfPass = true )
    public void failing_test_with_Pending_annotation_and_failIfPassed_set_to_true() {
        given().an_exception_is_thrown();
    }

    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    @Pending( failIfPass = true, executeSteps = true )
    public void failing_test_with_Pending_annotation_and_executeSteps_set_to_true() {
        given().an_exception_is_thrown();
    }

    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    @TestTag( "testValue" )
    public void test_with_tag_annotation() {
        given().nothing();
    }

}
