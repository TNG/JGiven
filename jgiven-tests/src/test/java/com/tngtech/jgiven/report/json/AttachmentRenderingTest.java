package com.tngtech.jgiven.report.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.report.html5.Html5ReportConfig;
import com.tngtech.jgiven.report.html5.Html5ReportGenerator;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.tags.Issue;
import com.tngtech.jgiven.testframework.TestExecutionResult;
import com.tngtech.jgiven.testframework.TestExecutor;
import com.tngtech.jgiven.testframework.TestFramework;
import com.tngtech.jgiven.tests.TestScenarioRepository;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.openqa.selenium.json.Json;

import static com.tngtech.jgiven.report.json.AttachmentRenderingTest.*;

@Issue("1131")
public class AttachmentRenderingTest extends SimpleScenarioTest<TestExecutionStages> {

    @Test
    public void testJsonIsRenderedCorrectly() {
        given().a_test_that_produces_many_attachments();
        when().the_test_gets_executed().and().the_report_gets_rendered();
        then().do_something();
    }

    @SuppressWarnings("UnusedReturnValue")
    static class TestExecutionStages extends Stage<TestExecutionStages> {
        private TestScenarioRepository.TestScenario testScenario;
        private ReportModel reportModel;
        private JsonObject reportJson;

        TestExecutionStages a_test_that_produces_many_attachments(){
            testScenario = TestScenarioRepository.attachmentRenderingTest();
            return self();
        }

        TestExecutionStages the_test_gets_executed(){
            TestExecutor executor = TestExecutor.getExecutor(TestFramework.JUnit);
            TestExecutionResult executionResult = executor.execute(testScenario.testClass);
            reportModel=executionResult.getReportModel();
            return self();
        }

        TestExecutionStages the_report_gets_rendered(){
            reportJson = JsonParser.parseString(new ScenarioJsonWriter(reportModel).toString()).getAsJsonObject();
            return self();
        }

        TestExecutionStages do_something(){
            //TODO: assertions
           System.out.println("tarlalsl");
           return self();
        }
    }
}
