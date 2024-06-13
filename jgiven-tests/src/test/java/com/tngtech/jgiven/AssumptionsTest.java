package com.tngtech.jgiven;


import com.tngtech.jgiven.testframework.ThenTestFramework;
import com.tngtech.jgiven.testframework.WhenTestFramework;
import com.tngtech.jgiven.tests.assumptions.JUnit5AssumptionTestScenarios;
import com.tngtech.jgiven.tests.assumptions.JUnitAssumptionTestScenarios;
import com.tngtech.jgiven.tests.assumptions.TestNgAssumptionTestScenarios;
import org.junit.Test;

public class AssumptionsTest extends JGivenScenarioTest<GivenScenarioTest<?>, WhenTestFramework<?>, ThenTestFramework<?>> {

    @Test
    public void testng_does_not_fail_for_failing_assumption() {
        testNg_handles_assumptions_correctly("test_with_failing_assumption");
    }

    @Test
    public void testng_does_not_fail_for_failing_assumption_in_stage() {
        testNg_handles_assumptions_correctly("test_with_failing_assumption_in_stage");
    }
    @Test
    public void testng_does_not_fail_for_failing_assumption_in_second_stage() {
        testNg_handles_assumptions_correctly("test_with_failing_assumption_in_second_stage");
    }

    private void testNg_handles_assumptions_correctly(String testName) {
        given().a_test_named_$(testName, TestNgAssumptionTestScenarios.class);
        when().the_test_is_executed_with_TestNG();
        then().the_test_is_ignored()
                .the_report_model_contains_$_scenarios(0);
    }

    @Test
    public void junit_does_not_fail_for_failing_assumption() {
        junit_handles_assumptions_correctly("test_with_failing_assumption");
    }

    @Test
    public void junit_does_not_fail_for_failing_assumption_in_stage() {
        junit_handles_assumptions_correctly("test_with_failing_assumption_in_stage");
    }

    @Test
    public void junit_does_not_fail_for_failing_assumption_in_second_stage() {
        junit_handles_assumptions_correctly("test_with_failing_assumption_in_second_stage");
    }

    private void junit_handles_assumptions_correctly(String testName) {
        given().a_test_named_$(testName, JUnitAssumptionTestScenarios.class);
        when().the_test_is_executed_with_JUnit();
        then().the_test_is_ignored()
                .the_report_model_contains_$_scenarios(0);
    }

    @Test
    public void junit5_does_not_fail_for_failing_assumption() {
        junit5_handles_assumptions_correctly("test_with_failing_assumption");
    }

    @Test
    public void junit5_does_not_fail_for_failing_assumption_in_stage() {
        junit5_handles_assumptions_correctly("test_with_failing_assumption_in_stage");
    }

    @Test
    public void junit5_does_not_fail_for_failing_assumption_in_second_stage() {
        junit5_handles_assumptions_correctly("test_with_failing_assumption_in_second_stage");
    }

    private void junit5_handles_assumptions_correctly(String testName) {
        given().a_test_named_$(testName, JUnit5AssumptionTestScenarios.class);
        when().the_test_is_executed_with_JUnit5();
        then().the_test_is_ignored()
                .the_report_model_contains_$_scenarios(1);
    }
}


