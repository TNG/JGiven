package com.tngtech.jgiven.testng;

import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.google.common.base.Throwables;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.impl.util.ParameterNameUtil;
import com.tngtech.jgiven.report.impl.CommonReportHelper;
import com.tngtech.jgiven.report.model.NamedArgument;
import com.tngtech.jgiven.report.model.ReportModel;

/**
 * TestNG Test listener to enable JGiven for a test class
 */
public class ScenarioTestListener implements ITestListener {
    private volatile ReportModel reportModel;

    private final Map<ITestResult, ScenarioBase> scenarioMap = Collections.synchronizedMap(
        new IdentityHashMap<ITestResult, ScenarioBase>() );

    @Override
    public void onTestStart( ITestResult paramITestResult ) {
        Object instance = paramITestResult.getInstance();
        reportModel.setTestClass( instance.getClass() );

        ScenarioBase scenario;

        if( instance instanceof ScenarioTestBase<?, ?, ?> ) {
            ScenarioTestBase<?, ?, ?> testInstance = (ScenarioTestBase<?, ?, ?>) instance;
            scenario = testInstance.createNewScenario();
        } else {
            scenario = new ScenarioBase();
        }

        scenarioMap.put( paramITestResult, scenario );

        scenario.setModel( reportModel );
        scenario.getExecutor().injectSteps( instance );

        Method method = paramITestResult.getMethod().getConstructorOrMethod().getMethod();
        scenario.startScenario( method, getArgumentsFrom( method, paramITestResult ) );

        // inject state from the test itself
        scenario.getExecutor().readScenarioState( instance );
    }

    @Override
    public void onTestSuccess( ITestResult paramITestResult ) {
        testFinished( paramITestResult );
    }

    @Override
    public void onTestFailure( ITestResult paramITestResult ) {
        ScenarioBase scenario = scenarioMap.get( paramITestResult );
        if( scenario != null ) {
            scenario.getExecutor().failed( paramITestResult.getThrowable() );
            testFinished( paramITestResult );
        }
    }

    @Override
    public void onTestSkipped( ITestResult paramITestResult ) {}

    private void testFinished( ITestResult paramITestResult ) {
        try {
            ScenarioBase scenario = scenarioMap.get( paramITestResult );
            scenario.finished();
        } catch( Throwable throwable ) {
            throw Throwables.propagate( throwable );
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage( ITestResult paramITestResult ) {}

    @Override
    public void onStart( ITestContext paramITestContext ) {
        reportModel = new ReportModel();
    }

    @Override
    public void onFinish( ITestContext paramITestContext ) {
        new CommonReportHelper().finishReport( reportModel );
    }

    private List<NamedArgument> getArgumentsFrom( Method method, ITestResult paramITestResult ) {
        return ParameterNameUtil.mapArgumentsWithParameterNames( method, asList( paramITestResult.getParameters() ) );
    }

}
