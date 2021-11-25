package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.tags.Issue;
import com.tngtech.jgiven.testframework.TestExecutionResult;
import com.tngtech.jgiven.testframework.TestExecutor;
import com.tngtech.jgiven.testframework.TestFramework;
import com.tngtech.jgiven.tests.StepTimingRecordingTest;
import com.tngtech.jgiven.tests.TestScenarioRepository;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;

@Issue("#755")
@RunWith(DataProviderRunner.class)
public class TimingsTest extends SimpleScenarioTest<TimingsTest.SimpleTestStage> {

    @Test
    @DataProvider({
        "last_step_is_preceeded_by_step",
        "last_step_is_preceeded_by_intro_word",
        "last_step_is_preceeded_by_filler_word",
        "last_step_is_succeeded_by_intro_word",
        "last_step_is_succeeded_by_filler_word",
    })
    public void recorded_timing_is_correct_for(String methodName) {
        given().the_JGiven_timings_test_class_with_method(methodName);
        when().the_test_is_executed();
        then().the_recorded_timing_is_greater_than_ten_millis();
    }

    @SuppressWarnings("UnusedReturnValue")
    static class SimpleTestStage extends Stage<SimpleTestStage> {
        TestScenarioRepository.TestScenario testScenario;
        private ReportModel testReport;

        SimpleTestStage the_JGiven_timings_test_class_with_method(String requestedMethod) {
            testScenario = new TestScenarioRepository.TestScenario(StepTimingRecordingTest.class, requestedMethod);
            assertThat(StepTimingRecordingTest.class.getMethods())
                .as("Requested method exists in class")
                .extracting(Method::getName)
                .contains(requestedMethod);
            return this;
        }

        SimpleTestStage the_test_is_executed() {
            TestExecutor testExecutor = TestExecutor.getExecutor(TestFramework.JUnit);
            TestExecutionResult testExecutionResult = testExecutor.execute(testScenario.testClass,
                testScenario.testMethod);

            testReport = testExecutionResult.getReportModel();
            return this;
        }

        SimpleTestStage the_recorded_timing_is_greater_than_ten_millis() {
            long tenMillisecondsInNanos = 10_000_000;
            List<StepModel> executedSteps =  testReport.getLastScenarioModel().getCase(0).getSteps();
            long actualDurationInNanos = executedSteps.get(executedSteps.size() - 1).getDurationInNanos();
            assertThat(actualDurationInNanos).isGreaterThan(tenMillisecondsInNanos);
            return this;
        }
    }
}
