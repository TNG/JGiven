package com.tngtech.jgiven;


import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.testframework.ThenTestFramework;
import com.tngtech.jgiven.testframework.WhenTestFramework;
import com.tngtech.jgiven.tests.TestScenarios;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class AssumptionsTest extends JGivenScenarioTest<GivenScenarioTest<?>, WhenTestFramework<?>, ThenTestFramework<?>> {

    @Test
    @DataProvider({
            "test_with_failing_testng_assumption",
            "test_with_failing_assertJ_assumption",
            "test_with_failing_testng_assumption_in_stage",
            "test_with_failing_assertJ_assumption_in_stage"
    })
    public void testNg_handles_assumptions_correctly(String testName) {
        given().a_test_named_$(testName, TestScenarios.class);
        when().the_test_is_executed_with_TestNG();
        then().the_test_is_ignored()
                .the_report_model_contains_$_scenarios(0);
    }

    @Test
    @DataProvider({
            //"test_with_failing_junit_assumption",
            //"test_with_failing_assertJ_assumption",
            "test_with_failing_junit_assumption_in_stage",
            "test_with_failing_assertJ_assumption_in_stage"
    })
    public void junit_handles_assumptions_correctly(String testName) {
        given().a_test_named_$(testName, TestScenarios.class);
        when().the_test_is_executed_with_JUnit();
        then().the_test_is_ignored()
                .the_report_model_contains_$_scenarios(0);
    }

    @Test
    @DataProvider({
            "test_with_failing_junit5_assumption",
            "test_with_failing_assertJ_assumption",
            "test_with_failing_junit5_assumption_in_stage",
            "test_with_failing_assertJ_assumption_in_stage"
    })
    public void junit5_handles_assumptions_correctly(String testName) {
        given().a_test_named_$(testName, TestScenarios.class);
        when().the_test_is_executed_with_JUnit5();
        then().the_test_is_ignored()
                .the_report_model_contains_$_scenarios(0);
    }
}


