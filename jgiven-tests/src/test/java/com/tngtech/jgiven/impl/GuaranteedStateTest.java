package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.exception.JGivenMissingGuaranteedScenarioStateException;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.testframework.TestExecutionResult;
import com.tngtech.jgiven.testframework.TestExecutor;
import com.tngtech.jgiven.testframework.TestFramework;
import com.tngtech.jgiven.tests.GuaranteedFieldRealTest;
import com.tngtech.jgiven.tests.TestScenarioRepository;
import org.junit.Test;

public class GuaranteedStateTest extends SimpleScenarioTest<GuaranteedStateTest.SimpleTestStage> {

    @Test
    public void assure_before_method_of_second_test_is_executed_after_guaranteed_fields_validation() {
        given().a_Jgiven_test_with_a_guaranteed_null_state();
        when().the_test_is_executed();
        then().the_report_contains_$_exception(JGivenMissingGuaranteedScenarioStateException.class);
    }

    @Test
    public void assure_before_method_of_second_test_is_executed_if_guaranteed_initialized() {
        given().a_Jgiven_test_with_a_guaranteed_state();
        when().the_test_is_executed();
        then().the_report_contains_$_exception(ClassNotFoundException.class);
    }

    public static class SimpleTestStage extends Stage<SimpleTestStage> {
        TestScenarioRepository.TestScenario testScenario;
        private ReportModel testReport;

        public void a_Jgiven_test_with_a_guaranteed_null_state() {
            testScenario = new TestScenarioRepository.TestScenario(GuaranteedFieldRealTest.class, "a_sample_test");
        }

        public void a_Jgiven_test_with_a_guaranteed_state() {
            testScenario = new TestScenarioRepository.TestScenario(GuaranteedFieldRealTest.class,
                                                            "a_sample_initialized_test");
        }

        public void the_test_is_executed() {
            TestExecutor testExecutor = TestExecutor.getExecutor(TestFramework.JUnit);
            TestExecutionResult testExecutionResult = testExecutor.execute(testScenario.testClass,
                                                                           testScenario.testMethod);

            testReport = testExecutionResult.getReportModel();
        }

        public void the_report_contains_$_exception(Class<? extends Exception> givenException) {
            assertThat(testReport.getFailedScenarios()).isNotEmpty();
            assertThat(testReport.getFailedScenarios().get(0)
                    .getScenarioCases().get(0).getErrorMessage()).contains(givenException.getName());
        }
    }
}
