package com.tngtech.jgiven.junit.de;

import org.junit.Test;

import com.tngtech.jgiven.GivenScenarioTest;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.tags.FeatureJUnit;
import com.tngtech.jgiven.testframework.ThenTestFramework;
import com.tngtech.jgiven.testframework.WhenTestFramework;

@FeatureJUnit
public class JUnitParameterizedRunnerTest extends JGivenScenarioTest<GivenScenarioTest<?>, WhenTestFramework<?>, ThenTestFramework<?>> {

    @Test
    public void the_JUnit_Parameterized_runner_creates_correct_cases() {
        given().a_JUnit_test_class_with_the_Parameterized_Runner()
            .and().the_test_class_has_$_parameters( 2 );
        when().the_test_class_is_executed_with_JUnit();
        then().the_report_model_contains_one_scenario_for_each_test_method()
            .and().each_scenario_contains_$_cases( 2 );
    }
}
