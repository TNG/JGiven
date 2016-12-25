package com.tngtech.jgiven.testng;

import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.tngtech.jgiven.impl.ScenarioHolder;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.impl.util.AssertionUtil;
import com.tngtech.jgiven.impl.util.ParameterNameUtil;
import com.tngtech.jgiven.report.impl.CommonReportHelper;
import com.tngtech.jgiven.report.model.NamedArgument;
import com.tngtech.jgiven.report.model.ReportModel;

/**
 * TestNG Test listener to enable JGiven for a test class
 */
public class ScenarioTestListener implements ITestListener {

    public static final String SCENARIO_ATTRIBUTE = "jgiven::scenario";

    private volatile ConcurrentMap<String, ReportModel> reportModels;

    @Override
    public void onTestStart( ITestResult paramITestResult ) {
        Object instance = paramITestResult.getInstance();

        ScenarioBase scenario;

        if( instance instanceof ScenarioTestBase<?, ?, ?> ) {
            ScenarioTestBase<?, ?, ?> testInstance = (ScenarioTestBase<?, ?, ?>) instance;
            scenario = testInstance.createNewScenario();
        } else {
            scenario = new ScenarioBase();
        }

        ScenarioHolder.get().setScenarioOfCurrentThread( scenario );
        paramITestResult.setAttribute(SCENARIO_ATTRIBUTE, scenario);

        ReportModel reportModel = getReportModel( instance.getClass() );
        scenario.setModel( reportModel );
        scenario.getExecutor().injectStages( instance );

        Method method = paramITestResult.getMethod().getConstructorOrMethod().getMethod();
        scenario.startScenario( instance.getClass(), method, getArgumentsFrom( method, paramITestResult ) );

        // inject state from the test itself
        scenario.getExecutor().readScenarioState( instance );
    }

    private ScenarioBase getScenario(ITestResult paramITestResult ) {
        return (ScenarioBase) paramITestResult.getAttribute(SCENARIO_ATTRIBUTE);
    }

    private ReportModel getReportModel( Class<?> clazz ) {
        ReportModel model = reportModels.get( clazz.getName() );
        if( model == null ) {
            model = new ReportModel();
            model.setTestClass( clazz );
            ReportModel previousModel = reportModels.putIfAbsent( clazz.getName(), model );
            if( previousModel != null ) {
                model = previousModel;
            }
        }
        AssertionUtil.assertNotNull( model, "Report model is null" );
        return model;
    }

    @Override
    public void onTestSuccess( ITestResult paramITestResult ) {
        testFinished( paramITestResult );
    }

    @Override
    public void onTestFailure( ITestResult paramITestResult ) {
        ScenarioBase scenario = getScenario( paramITestResult );
        if( scenario != null ) {
            scenario.getExecutor().failed( paramITestResult.getThrowable() );
            testFinished( paramITestResult );
        }
    }

    @Override
    public void onTestSkipped( ITestResult paramITestResult ) {}

    private void testFinished( ITestResult paramITestResult ) {
        try {
            ScenarioBase scenario = getScenario( paramITestResult );
            scenario.finished();
        } catch( Throwable throwable ) {
            paramITestResult.setThrowable( throwable );
            paramITestResult.setStatus( ITestResult.FAILURE );
        } finally {
            ScenarioHolder.get().removeScenarioOfCurrentThread();
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage( ITestResult paramITestResult ) {}

    @Override
    public void onStart( ITestContext paramITestContext ) {
        reportModels = new ConcurrentHashMap<String, ReportModel>();
    }

    @Override
    public void onFinish( ITestContext paramITestContext ) {
        for( ReportModel reportModel : reportModels.values() ) {
            new CommonReportHelper().finishReport( reportModel );
        }
    }

    private List<NamedArgument> getArgumentsFrom( Method method, ITestResult paramITestResult ) {
        return ParameterNameUtil.mapArgumentsWithParameterNames( method, asList( paramITestResult.getParameters() ) );
    }

}
