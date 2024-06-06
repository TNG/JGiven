package com.tngtech.jgiven;


import com.tngtech.jgiven.testframework.ThenTestFramework;
import com.tngtech.jgiven.testframework.WhenTestFramework;
import com.tngtech.jgiven.tests.TestScenarios;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AssumptionsTest extends JGivenScenarioJunit5Test<GivenScenarioTest<?>, WhenTestFramework<?>, ThenTestFramework<?>> {

    @ParameterizedTest
    @ValueSource(strings = {
            "test_with_failing_testng_assumption",
            "test_with_failing_assertJ_assumption",
            "test_with_failing_testng_assumption_in_stage",
            "test_with_failing_assertJ_assumption_in_stage"
    })
    void testNg_handles_assumptions_correctly(String testName) {
        given().a_test_named_$(testName, TestScenarios.class);
        when().the_test_is_executed_with_TestNG();
        then().the_test_is_ignored()
                .the_report_model_contains_$_scenarios(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test_with_failing_junit_assumption",
            "test_with_failing_assertJ_assumption",
            "test_with_failing_junit_assumption_in_stage",
            "test_with_failing_assertJ_assumption_in_stage"
    })
    void junit_handles_assumptions_correctly(String testName) {
        given().a_test_named_$(testName, TestScenarios.class);
        when().the_test_is_executed_with_JUnit();
        then().the_test_is_ignored()
                .the_report_model_contains_$_scenarios(0);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "test_with_failing_junit_assumption",
            "test_with_failing_assertJ_assumption",
            "test_with_failing_junit_assumption_in_stage",
            "test_with_failing_assertJ_assumption_in_stage"
    })
    void junit5_handles_assumptions_correctly(String testName) {
        given().a_test_named_$(testName, TestScenarios.class);
        when().the_test_is_executed_with_JUnit5();
        then().the_test_is_ignored()
                .the_report_model_contains_$_scenarios(0);
    }
}


