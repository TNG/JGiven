package com.tngtech.jgiven.junit;

import org.junit.Test;

import com.tngtech.jgiven.GivenScenarioTest;
import com.tngtech.jgiven.JGivenTestConfiguration;
import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.tags.FeatureJUnit;
import com.tngtech.jgiven.tags.FeatureNotImplementedYet;
import com.tngtech.jgiven.tags.FeatureTags;
import com.tngtech.jgiven.tags.Issue;

@FeatureJUnit
@JGivenConfiguration( JGivenTestConfiguration.class )
public class JUnitExecutionTest extends ScenarioTest<GivenScenarioTest<?>, WhenJUnitTest<?>, ThenJUnitTest<?>> {

    @Test
    @FeatureNotImplementedYet
    public void failing_tests_annotated_with_NotImplementedYet_are_ignored() {
        given().a_failing_test()
            .and().the_test_is_annotated_with_NotImplementedYet();
        when().the_test_is_executed_with_JUnit();
        then().the_test_is_ignored();
    }

    @Test
    @FeatureNotImplementedYet
    public void passing_tests_annotated_with_NotImplementedYet_are_ignored() {
        given().a_passing_test()
            .and().the_test_is_annotated_with_NotImplementedYet();
        when().the_test_is_executed_with_JUnit();
        then().the_test_is_ignored();
    }

    @Test
    @Issue( "#4" )
    @FeatureNotImplementedYet
    public void passing_tests_annotated_with_NotImplementedYet_with_failIfPassed_set_to_true_fail() {
        given().a_passing_test()
            .and().the_test_is_annotated_with_NotImplementedYet()
            .with().failIfPassed_set_to_true();
        when().the_test_is_executed_with_JUnit();
        then().the_test_fails_with_message( "Test succeeded, but failIfPassed set to true" );
    }

    @Test
    @Issue( "#4" )
    @FeatureNotImplementedYet
    public void failing_tests_annotated_with_NotImplementedYet_with_failIfPassed_set_to_true_are_ignored() {
        given().a_failing_test()
            .and().the_test_is_annotated_with_NotImplementedYet()
            .with().failIfPassed_set_to_true();
        when().the_test_is_executed_with_JUnit();
        then().the_test_is_ignored();
    }

    @Test
    @FeatureNotImplementedYet
    public void failing_tests_annotated_with_NotImplementedYet_with_executeSteps_set_to_true_are_ignored() {
        given().a_failing_test()
            .and().the_test_is_annotated_with_NotImplementedYet()
            .with().executeSteps_set_to_true();
        when().the_test_is_executed_with_JUnit();
        then().the_test_is_ignored();
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
    public void passing_steps_before_failing_steps_are_reported_as_passed() {
        given().a_failing_test_with_$_steps( 2 )
            .and().step_$_fails( 2 );
        when().the_test_is_executed_with_JUnit();
        then().step_$_is_reported_as_passed( 1 )
            .and().step_$_is_reported_as_failed( 2 );
    }

    @Test
    public void the_error_message_of_a_failing_step_is_reported() {
        given().a_failing_test();
        when().the_test_is_executed_with_JUnit();
        then().the_case_is_marked_as_failed()
            .and().an_error_message_is_stored_in_the_report();
    }

    @Test
    @FeatureTags
    public void tag_annotations_appear_in_the_report_model() {
        given().a_test()
            .and().the_test_has_a_tag_annotation_named( "TestTag" );
        when().the_test_is_executed_with_JUnit();
        then().the_report_model_contains_a_tag_named( "TestTag" );
    }
}
