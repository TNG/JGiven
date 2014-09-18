package com.tngtech.jgiven.testng;

import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.util.List;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.NamedArgument;
import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.impl.util.ScenarioUtil;
import com.tngtech.jgiven.report.impl.CommonReportHelper;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelBuilder;

/**
 * TestNG Test listener to enable JGiven for a test class
 */
public class ScenarioTestListener implements ITestListener {
    private ReportModel scenarioCollectionModel;

    private ScenarioBase scenario;

    @Override
    public void onTestStart( ITestResult paramITestResult ) {
        Object instance = paramITestResult.getInstance();
        ReportModelBuilder builder = new ReportModelBuilder( scenarioCollectionModel );
        builder.setTestClass( instance.getClass() );

        if( instance instanceof ScenarioTestBase<?, ?, ?> ) {
            ScenarioTestBase<?, ?, ?> testInstance = (ScenarioTestBase<?, ?, ?>) instance;
            scenario = testInstance.createNewScenario();
        } else {
            scenario = new ScenarioBase();
        }
        scenario.setModel( scenarioCollectionModel );

        Method method = paramITestResult.getMethod().getConstructorOrMethod().getMethod();
        scenario.getExecutor().startScenario( method, getArgumentsFrom( method, paramITestResult ) );
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

    private List<NamedArgument> getArgumentsFrom( Method method, ITestResult paramITestResult ) {
        return ScenarioUtil.mapArgumentsWithParameterNames( method, asList( paramITestResult.getParameters() ) );
    }

}
