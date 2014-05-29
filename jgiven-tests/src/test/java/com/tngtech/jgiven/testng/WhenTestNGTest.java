package com.tngtech.jgiven.testng;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.Config;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.tests.TestScenarioRepository.TestScenario;

public class WhenTestNGTest<SELF extends WhenTestNGTest<?>> extends Stage<SELF> {

    @ExpectedScenarioState
    TestScenario testScenario;

    @ProvidedScenarioState
    ReportModel reportModel;

    @ProvidedScenarioState
    ITestResult testResult;

    static String methodName;

    public void the_test_is_executed_with_TestNG() {
        ScenarioTestListenerAdapter testListenerAdapter = new ScenarioTestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses( new Class<?>[] { testScenario.testClass } );
        methodName = testScenario.testMethod;
        testng.addMethodSelector( MethodSelector.class.getName(), 10 );
        testng.addListener( testListenerAdapter );
        Config.config().setReportEnabled( false );
        testng.run();
        Config.config().setReportEnabled( true );
        this.reportModel = testListenerAdapter.reportModel;
        this.testResult = testListenerAdapter.testResult;
    }

    static class ScenarioTestListenerAdapter extends TestListenerAdapter {
        ReportModel reportModel;
        ITestResult testResult;

        @Override
        public void onTestSuccess( ITestResult tr ) {
            setTestResult( tr );
        }

        @Override
        public void onTestFailure( ITestResult tr ) {
            setTestResult( tr );
        }

        private void setTestResult( ITestResult tr ) {
            testResult = tr;
            reportModel = ( (ScenarioTestBase<?, ?, ?>) tr.getInstance() ).getScenario().getModel();
        }
    }
}
