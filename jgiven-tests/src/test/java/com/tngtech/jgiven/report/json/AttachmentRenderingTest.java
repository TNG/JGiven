package com.tngtech.jgiven.report.json;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.report.model.AttachmentModel;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.tags.Issue;
import com.tngtech.jgiven.testframework.TestExecutionResult;
import com.tngtech.jgiven.testframework.TestExecutor;
import com.tngtech.jgiven.testframework.TestFramework;
import com.tngtech.jgiven.tests.TestScenarioRepository;
import org.junit.Test;

import java.util.List;

import static com.tngtech.jgiven.report.json.AttachmentRenderingTest.TestExecutionStages;
import static org.assertj.core.api.Assertions.assertThat;

@Issue("1131")
public class AttachmentRenderingTest extends SimpleScenarioTest<TestExecutionStages> {

    @Test
    public void testJsonIsRenderedCorrectly() {
        given().a_test_where_every_terminal_step_has_an_attachment_of_its_name();
        when().the_test_gets_executed();
        then().for_each_terminal_step_the_report_has_an_attachment_of_the_method_name();
    }

    @SuppressWarnings("UnusedReturnValue")
    static class TestExecutionStages extends Stage<TestExecutionStages> {
        private TestScenarioRepository.TestScenario testScenario;
        private ReportModel reportModel;

        TestExecutionStages a_test_where_every_terminal_step_has_an_attachment_of_its_name(){
            testScenario = TestScenarioRepository.attachmentRenderingTest();
            return self();
        }

        TestExecutionStages the_test_gets_executed(){
            TestExecutor executor = TestExecutor.getExecutor(TestFramework.JUnit);
            TestExecutionResult executionResult = executor.execute(testScenario.testClass);
            reportModel=executionResult.getReportModel();
            return self();
        }

        TestExecutionStages for_each_terminal_step_the_report_has_an_attachment_of_the_method_name(){
            assertThat(reportModel.getScenarios())
                    .hasSize(1)
                    .extracting(ScenarioModel::getScenarioCases)
                    .extracting(List::size)
                    .containsExactly(1);
            reportModel.getLastScenarioModel().getCase(0).getSteps().forEach(this::assertStep);
            return self();
        }

        private void assertStep(StepModel step){
            if(!step.getNestedSteps().isEmpty()) {
                step.getNestedSteps().forEach(this::assertStep);
            } else {
                assertThat(step.getAttachments())
                        .extracting(AttachmentModel::getValue)
                        .containsExactly(step.getName());
            }
        }
    }
}
