package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.exception.JGivenMissingGuaranteedScenarioStateException;
import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.testframework.TestExecutor;
import com.tngtech.jgiven.testframework.TestFramework;
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

    public static class RealTest extends ScenarioTest<RealTest.RealGiven, RealTest.RealWhen, RealTest.RealThen> {

        @Test
        @SuppressWarnings("checkstyle:MethodName")
        public void a_sample_test() {
            given().a_sample_uninitialized_stage();
            when().I_do_something();
            then().I_did_something();
        }

        @Test
        @SuppressWarnings("checkstyle:MethodName")
        public void a_sample_initialized_test() {
            given().a_sample_initialized_stage();
            when().I_do_something();
            then().I_did_something();
        }

        public static class RealGiven extends Stage<RealGiven> {
            @ProvidedScenarioState(guaranteed = true)
            Object guaranteedObject = null;

            @SuppressWarnings("checkstyle:MethodName")
            public void a_sample_uninitialized_stage() {
            }

            @SuppressWarnings("checkstyle:MethodName")
            public void a_sample_initialized_stage() {
                this.guaranteedObject = "I'm initialized";
            }
        }

        public static class RealThen extends Stage<RealGiven> {
            @SuppressWarnings("checkstyle:MethodName")
            public void I_did_something() {
            }
        }

        public static class RealWhen extends Stage<RealGiven> {
            @BeforeStage
            public void beforeSetup() throws ClassNotFoundException {
                throw new ClassNotFoundException("Not a JGiven exception");
            }

            @SuppressWarnings("checkstyle:MethodName")
            public void I_do_something() {
            }
        }
    }

    public static class SimpleTestStage extends Stage<SimpleTestStage> {
        TestScenarioRepository.TestScenario testScenario;
        private ReportModel testReport;

        @SuppressWarnings("checkstyle:MethodName")
        public void a_Jgiven_test_with_a_guaranteed_null_state() {
            testScenario = new TestScenarioRepository.TestScenario(RealTest.class, "a_sample_test");
        }

        @SuppressWarnings("checkstyle:MethodName")
        public void a_Jgiven_test_with_a_guaranteed_state() {
            testScenario = new TestScenarioRepository.TestScenario(RealTest.class, "a_sample_initialized_test");
        }

        public void the_test_is_executed() {
            testReport = TestExecutor.getExecutor(TestFramework.JUnit)
                                .execute(testScenario.testClass, testScenario.testMethod).getReportModel();
        }

        @SuppressWarnings("checkstyle:MethodName")
        public void the_report_contains_$_exception(Class<? extends Exception> givenException) {
            assertThat(testReport.getFailedScenarios()).isNotEmpty();
            assertThat(testReport.getFailedScenarios().get(0)
                    .getScenarioCases().get(0).getErrorMessage()).contains(givenException.getName());
        }

    }
}
