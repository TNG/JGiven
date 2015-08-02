package com.tngtech.jgiven.testframework;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.tngtech.jgiven.GivenScenarioTest;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.tags.FeatureJUnit;
import com.tngtech.jgiven.tags.FeaturePending;
import com.tngtech.jgiven.tags.FeatureTags;
import com.tngtech.jgiven.tags.FeatureTestNg;
import com.tngtech.jgiven.tags.Issue;

@FeatureJUnit
@FeatureTestNg
@RunWith( Parameterized.class )
public class TestFrameworkExecutionTest extends JGivenScenarioTest<GivenScenarioTest<?>, WhenTestFramework<?>, ThenTestFramework<?>> {

    public final TestFramework testFramework;

    @Parameters
    public static Iterable<Object[]> testFrameworks() {
        return Arrays.asList( new Object[][] {
            { TestFramework.JUnit },
            { TestFramework.TestNG },
        } );
    }

    public TestFrameworkExecutionTest( TestFramework testFramework ) {
        this.testFramework = testFramework;
    }

    @Test
    @FeaturePending
    public void failing_tests_annotated_with_NotImplementedYet_are_ignored() {
        given().a_failing_test()
            .and().the_test_is_annotated_with_NotImplementedYet();
        when().the_test_is_executed_with( testFramework );
        then().the_test_is_ignored();
    }

    @Test
    @FeaturePending
    public void passing_tests_annotated_with_NotImplementedYet_are_ignored() {
        given().a_passing_test()
            .and().the_test_is_annotated_with_NotImplementedYet();
        when().the_test_is_executed_with( testFramework );
        then().the_test_is_ignored();
    }

    @Test
    @Issue( "#4" )
    @FeaturePending
    public void passing_tests_annotated_with_NotImplementedYet_with_failIfPassed_set_to_true_fail() {
        given().a_passing_test()
            .and().the_test_is_annotated_with_NotImplementedYet()
            .with().failIfPassed_set_to_true();
        when().the_test_is_executed_with( testFramework );
        then().the_test_fails_with_message( "Test succeeded, but failIfPassed set to true" );
    }

    @Test
    @Issue( "#4" )
    @FeaturePending
    public void failing_tests_annotated_with_NotImplementedYet_with_failIfPassed_set_to_true_are_ignored() {
        given().a_failing_test()
            .and().the_test_is_annotated_with_NotImplementedYet()
            .with().failIfPassed_set_to_true();
        when().the_test_is_executed_with( testFramework );
        then().the_test_is_ignored();
    }

    @Test
    @FeaturePending
    public void failing_tests_annotated_with_NotImplementedYet_with_executeSteps_set_to_true_are_ignored() {
        given().a_failing_test()
            .and().the_test_is_annotated_with_NotImplementedYet()
            .with().executeSteps_set_to_true();
        when().the_test_is_executed_with( testFramework );
        then().the_test_is_ignored();
    }

    @Test
    public void steps_following_failing_steps_are_reported_as_skipped() {
        given().a_failing_test_with_$_steps( 3 )
            .and().step_$_fails( 1 );
        when().the_test_is_executed_with( testFramework );
        then().step_$_is_reported_as_failed( 1 )
            .and().step_$_is_reported_as_skipped( 2 )
            .and().step_$_is_reported_as_skipped( 3 );
    }

    @Test
    public void passing_steps_before_failing_steps_are_reported_as_passed() {
        given().a_failing_test_with_$_steps( 2 )
            .and().step_$_fails( 2 );
        when().the_test_is_executed_with( testFramework );
        then().step_$_is_reported_as_passed( 1 )
            .and().step_$_is_reported_as_failed( 2 );
    }

    @Test
    public void the_error_message_of_a_failing_step_is_reported() {
        given().a_failing_test();
        when().the_test_is_executed_with( testFramework );
        then().the_case_is_marked_as_failed()
            .and().an_error_message_is_stored_in_the_report();
    }

    @Test
    public void all_steps_of_stages_following_failing_stages_are_ignored() {
        given().a_failing_test_with_$_steps( 2 )
            .and().the_test_has_$_failing_stages( 2 )
            .and().step_$_fails( 1 );
        when().the_test_is_executed_with( testFramework );
        then().the_test_fails()
            .and().step_$_is_reported_as_failed( 1 )
            .and().step_$_is_reported_as_skipped( 2 );
    }

    @Test
    public void after_stage_methods_of_stages_following_failing_stages_are_ignored() {
        given().a_failing_test_with_$_steps( 2 )
            .and().the_test_has_$_failing_stages( 2 )
            .and().stage_$_has_a_failing_after_stage_method( 2 )
            .and().step_$_fails( 1 );
        when().the_test_is_executed_with( testFramework );
        then().the_test_fails()
            .and().step_$_is_reported_as_failed( 1 )
            .and().step_$_is_reported_as_skipped( 2 );
    }

    @Test
    @FeatureTags
    public void tag_annotations_appear_in_the_report_model() {
        given().a_test()
            .and().the_test_has_a_tag_annotation_named( "TestTag" );
        when().the_test_is_executed_with( testFramework );
        then().the_report_model_contains_a_tag_named( "TestTag" );
    }

    @Test
    public void description_annotations_on_test_classes_are_evaluated() {
        given().a_test_class()
            .and().the_test_class_has_a_description_annotation_with_value( "Test Description" );
        when().the_test_is_executed_with( testFramework );
        then().the_description_of_the_report_model_is( "Test Description" );
    }
}
