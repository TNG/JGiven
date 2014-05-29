package com.tngtech.jgiven.junit;

import org.junit.Test;

import com.tngtech.jgiven.GivenScenarioTest;
import com.tngtech.jgiven.tags.FeatureJUnit;
import com.tngtech.jgiven.tags.FeatureNotImplementedYet;

@FeatureJUnit
public class JUnitExecutionTest extends ScenarioTest<GivenScenarioTest<?>, WhenJUnitTest<?>, ThenJUnitTest<?>> {

    @Test
    @FeatureNotImplementedYet
    public void failing_tests_annotated_with_NotImplementedYet_are_ignored() {
        given().a_failing_test()
            .and().the_test_is_annotated_with_NotImplementedYet();
        when().the_test_is_executed_with_JUnit();
        then().the_test_passes();
    }

    @Test
    @FeatureNotImplementedYet
    public void passing_tests_annotated_with_NotImplementedYet_are_ignored() {
        given().a_passing_test()
            .and().the_test_is_annotated_with_NotImplementedYet();
        when().the_test_is_executed_with_JUnit();
        then().the_test_passes();
    }

    @Test
    public void steps_following_failing_steps_are_reported_as_skipped() {
        given().a_failing_test_with_$_steps( 2 )
            .and().step_$_fails( 1 );
        when().the_test_is_executed_with_JUnit();
        then().step_$_is_reported_as_failed( 1 )
            .and().step_$_is_reported_as_skipped( 2 );
    }

    @Test
    public void the_error_message_of_a_failing_step_is_reported() {
        given().a_failing_test();
        when().the_test_is_executed_with_JUnit();
        then().an_error_message_is_stored_in_the_report();
    }
}
