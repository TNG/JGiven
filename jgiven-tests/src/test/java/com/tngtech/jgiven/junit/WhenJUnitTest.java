package com.tngtech.jgiven.junit;

import org.assertj.core.api.Assertions;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.impl.Config;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.tests.TestScenarioRepository.TestScenario;

public class WhenJUnitTest<SELF extends WhenJUnitTest<?>> extends Stage<SELF> {

    @ExpectedScenarioState
    TestScenario testScenario;

    @ProvidedScenarioState
    Result result;

    @ProvidedScenarioState
    ReportModel reportModel;

    public void the_test_is_executed_with_JUnit() {
        Assertions.assertThat( testScenario ).as( "No matching test scenario found" ).isNotNull();

        JUnitCore junitCore = new JUnitCore();
        Request request = Request.method( testScenario.testClass, testScenario.testMethod );
        TestRunListener runListener = new TestRunListener();
        junitCore.addListener( runListener );
        Config.config().setReportEnabled( false );
        result = junitCore.run( request );
        Config.config().setReportEnabled( true );
        reportModel = runListener.reportModel;
    }

    static class TestRunListener extends RunListener {
        ReportModel reportModel;

        @Override
        public void testStarted( Description description ) throws Exception {
            reportModel = ScenarioTest.writerRule.getTestCaseModel();
        }

    }
}
