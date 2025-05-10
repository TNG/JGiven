package com.tngtech.jgiven.testframework;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.ThenReportModel;
import com.tngtech.jgiven.tests.TestScenarioRepository.TestScenario;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue") //JGiven style prefers a fluent interface
public class ThenTestFramework<SELF extends ThenTestFramework<?>> extends ThenReportModel<SELF> {
    @ExpectedScenarioState
    protected TestScenario testScenario;

    @ExpectedScenarioState
    TestExecutionResult result;

    public SELF the_test_is_ignored() {
        // this is actually not correct, because it depends on the JUnit executor whether
        // a test is ignored if an AssumptionException is thrown.
        // The standard JUnit executor will report the test as passed and not ignored,
        // we thus only test for not failed here
        the_test_passes();
        assertThat(reportModel.getScenarios().get(0).getExecutionStatus())
                .isIn(ExecutionStatus.ABORTED, ExecutionStatus.SCENARIO_PENDING, ExecutionStatus.SOME_STEPS_PENDING);
        return self();
    }

    public SELF the_test_passes() {
        assertThat(result.getFailureCount()).as("failure count").isEqualTo(0);
        return self();
    }

    public SELF $_tests_fail(int nFailedTests) {
        assertThat(result.getFailureCount()).isEqualTo(nFailedTests);
        return self();
    }

    public SELF the_test_fails() {
        assertThat(result.getFailureCount()).as("failure count").isGreaterThan(0);
        return self();
    }

    public SELF the_test_fails_with_message(String expectedMessage) {
        the_test_fails();
        assertThat(result.getFailureMessage(0)).as("failure message").contains(expectedMessage);
        return self();
    }

    public SELF the_report_model_contains_one_scenario_for_each_test_method() {
        List<Method> nonStaticMethods = ReflectionUtil.getNonStaticMethod(testScenario.testClass.getDeclaredMethods());
        assertThat(reportModel.getScenarios()).hasSize(nonStaticMethods.size());
        return self();
    }

    public SELF each_scenario_contains_$_cases(int nParameters) {
        for (ScenarioModel scenario : reportModel.getScenarios()) {
            assertThat(scenario.getScenarioCases()).hasSize(nParameters);
        }
        return self();
    }

    public SELF has_a_valid_class_name_if_it_is_not_null() {
        if (reportModel != null) {
            the_report_model_has_a_valid_class_name();
        }
        return self();
    }

    public SELF the_report_model_has_a_valid_class_name() {
        assertThat(reportModel.getClassName()).isEqualTo(testScenario.testClass.getName());
        return self();
    }

    public SELF the_scenario_has_execution_status(ExecutionStatus status) {
        assertThat(reportModel.getLastScenarioModel().getExecutionStatus()).isEqualTo(status);
        return self();
    }
}
