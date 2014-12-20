package com.tngtech.jgiven.junit;

import org.junit.Test;

import com.tngtech.jgiven.GivenScenarioTest;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.tags.FeatureJUnit;
import com.tngtech.jgiven.tags.Issue;
import com.tngtech.jgiven.testframework.ThenTestFramework;
import com.tngtech.jgiven.testframework.WhenTestFramework;

@FeatureJUnit
public class JUnitExecutorTest extends JGivenScenarioTest<GivenScenarioTest<?>, WhenTestFramework<?>, ThenTestFramework<?>> {

    @Test
    public void the_JUnit_Parameterized_runner_creates_correct_cases() {
        given().a_JUnit_test_class_with_the_Parameterized_Runner()
            .and().the_test_class_has_$_parameters( 2 );
        when().the_test_class_is_executed_with_JUnit();
        then().the_report_model_contains_one_scenario_for_each_test_method()
            .and().each_scenario_contains_$_cases( 2 );
    }

    @Test
    @Issue( "#25" )
    public void test_classes_with_only_ignored_test_result_in_a_valid_report() {
        given().a_test_class_with_all_tests_ignored();
        when().the_test_class_is_executed_with_JUnit();
        then().the_report_model_contains_$_scenarios( 0 )
            .and().the_report_model_has_a_valid_class_name();
    }

    @Test
    @Issue( "#49" )
    public void exception_in_scenario_is_not_hidden_by_exception_in_JUnit_after_method() {
        given().a_test_class_with_a_failing_scenario_and_a_failing_after_stage();
        when().the_test_class_is_executed_with_JUnit();
        then().the_test_fails_with_message( "assertion failed in test step" );
    }

}
