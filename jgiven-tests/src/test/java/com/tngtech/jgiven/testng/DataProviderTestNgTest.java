package com.tngtech.jgiven.testng;

import org.junit.Test;

import com.tngtech.jgiven.GivenScenarioTest;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.tags.FeatureTestNg;
import com.tngtech.jgiven.tags.Issue;
import com.tngtech.jgiven.testframework.TestFramework;
import com.tngtech.jgiven.testframework.ThenTestFramework;
import com.tngtech.jgiven.testframework.WhenTestFramework;

@FeatureTestNg
public class DataProviderTestNgTest extends JGivenScenarioTest<GivenScenarioTest<?>, WhenTestFramework<?>, ThenTestFramework<?>> {

    @Test
    @Issue( "#123" )
    public void a_scenario_with_one_failing_case_still_executes_the_following_ones() {
        given().a_TestNG_test_with_two_cases_and_the_first_one_fails();
        when().the_test_class_is_executed_with( TestFramework.TestNG );
        then().$_tests_fail( 1 )
            .and().the_report_model_contains_one_scenario_with_$_cases( 2 )
            .and().the_scenario_has_execution_status( ExecutionStatus.FAILED )
            .and().case_$_has_status( 1, ExecutionStatus.FAILED )
            .and().case_$_has_status( 2, ExecutionStatus.SUCCESS );
    }
}
