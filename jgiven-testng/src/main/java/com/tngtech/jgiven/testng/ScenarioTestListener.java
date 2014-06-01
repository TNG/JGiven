package com.tngtech.jgiven.testng;

import java.util.Arrays;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.internal.ConstructorOrMethod;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.report.impl.CommonReportHelper;
import com.tngtech.jgiven.report.model.ReportModel;

/**
 * TestNG Test listener to enable JGiven for a test class
 */
public class ScenarioTestListener implements ITestListener {
    private ReportModel scenarioCollectionModel;

    private ScenarioBase scenario;

    @Override
    public void onTestStart( ITestResult paramITestResult ) {
        Object instance = paramITestResult.getInstance();
        scenarioCollectionModel.className = instance.getClass().getName();

        if( instance instanceof ScenarioTestBase<?, ?, ?> ) {
            ScenarioTestBase<?, ?, ?> testInstance = (ScenarioTestBase<?, ?, ?>) instance;
            scenario = testInstance.createNewScenario();
        } else {
            scenario = new ScenarioBase();
        }
        scenario.setModel( scenarioCollectionModel );
        ConstructorOrMethod constructorOrMethod = paramITestResult.getMethod().getConstructorOrMethod();
        scenario.getExecutor().startScenario( constructorOrMethod.getMethod(), Arrays.asList( paramITestResult.getParameters() ) );
    }

    @Override
    public void onTestSuccess( ITestResult paramITestResult ) {
        testFinished();
    }

    @Override
    public void onTestFailure( ITestResult paramITestResult ) {
        scenario.getExecutor().failed( paramITestResult.getThrowable() );
        testFinished();
    }

    @Override
    public void onTestSkipped( ITestResult paramITestResult ) {}

    private void testFinished() {
        scenario.finished();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage( ITestResult paramITestResult ) {}

    @Override
    public void onStart( ITestContext paramITestContext ) {
        scenarioCollectionModel = new ReportModel();
    }

    @Override
    public void onFinish( ITestContext paramITestContext ) {
        new CommonReportHelper().finishReport( scenarioCollectionModel );
    }

}
