package com.tngtech.jgiven.junit;

import org.junit.Test;

import com.tngtech.jgiven.GivenScenarioTest;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.tags.FeatureJUnit;
import com.tngtech.jgiven.testframework.ThenTestFramework;
import com.tngtech.jgiven.testframework.WhenTestFramework;

@FeatureJUnit
public class DataProviderTest extends JGivenScenarioTest<GivenScenarioTest<?>, WhenTestFramework<?>, ThenTestFramework<?>> {

    @Test
    public void a_scenario_with_one_failing_case_leads_to_a_failed_scenario() {
        given().a_test_with_two_cases_and_the_first_one_fails();
        when().the_test_class_is_executed_with_JUnit();
        then().the_scenario_has_execution_status( ExecutionStatus.FAILED );
    }

}
