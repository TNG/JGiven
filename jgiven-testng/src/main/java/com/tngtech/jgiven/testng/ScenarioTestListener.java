package com.tngtech.jgiven.testng;

import static java.util.Arrays.asList;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.exception.FailIfPassedException;
import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.impl.ScenarioHolder;
import com.tngtech.jgiven.impl.util.AssertionUtil;
import com.tngtech.jgiven.impl.util.ParameterNameUtil;
import com.tngtech.jgiven.report.impl.CommonReportHelper;
import com.tngtech.jgiven.report.model.NamedArgument;
import com.tngtech.jgiven.report.model.ReportModel;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG Test listener to enable JGiven for a test class.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class ScenarioTestListener implements ITestListener {

    public static final String SCENARIO_ATTRIBUTE = "jgiven::scenario";
    public static final String REPORT_MODELS_ATTRIBUTE = "jgiven::reportModels";

    @Override
    public void onTestStart(ITestResult paramITestResult) {
        Object instance = paramITestResult.getInstance();

        ScenarioBase scenario;

        if (instance instanceof ScenarioTestBase<?, ?, ?>) {
            ScenarioTestBase<?, ?, ?> testInstance = (ScenarioTestBase<?, ?, ?>) instance;
            scenario = testInstance.createNewScenario();
        } else {
            scenario = new ScenarioBase();
        }

        ScenarioHolder.get().setScenarioOfCurrentThread(scenario);
        paramITestResult.setAttribute(SCENARIO_ATTRIBUTE, scenario);

        ReportModel reportModel = getReportModel(paramITestResult, instance.getClass());
        scenario.setModel(reportModel);

        //TestNG cannot run in parallel if stages are to be injected, because then multiple scenarios
        //will attempt to inject into a single test instance at the same time.
        new IncompatibleMultithreadingChecker().checkIncompatibleMultiThreading(paramITestResult);

        // TestNG does not work well when catching step exceptions, so we have to disable that feature
        // this mainly means that steps following a failing step are not reported in JGiven
        scenario.getExecutor().setSuppressStepExceptions(false);

        // avoid rethrowing exceptions as they are already thrown by the steps
        scenario.getExecutor().setSuppressExceptions(true);

        scenario.getExecutor().injectStages(instance);

        Method method = paramITestResult.getMethod().getConstructorOrMethod().getMethod();
        scenario.startScenario(instance.getClass(), method, getArgumentsFrom(method, paramITestResult));

        // inject state from the test itself
        scenario.getExecutor().readScenarioState(instance);
    }

    private ScenarioBase getScenario(ITestResult paramITestResult) {
        return (ScenarioBase) paramITestResult.getAttribute(SCENARIO_ATTRIBUTE);
    }

    private ReportModel getReportModel(ITestResult testResult, Class<?> clazz) {
        ConcurrentHashMap<String, ReportModel> reportModels = getReportModels(testResult.getTestContext());

        ReportModel model = reportModels.get(clazz.getName());
        if (model == null) {
            model = new ReportModel();
            model.setTestClass(clazz);
            ReportModel previousModel = reportModels.putIfAbsent(clazz.getName(), model);
            if (previousModel != null) {
                model = previousModel;
            }
        }
        AssertionUtil.assertNotNull(model, "Report model is null");
        return model;
    }

    @Override
    public void onTestSuccess(ITestResult paramITestResult) {
        testFinished(paramITestResult);
    }

    @Override
    public void onTestFailure(ITestResult paramITestResult) {
        ScenarioBase scenario = getScenario(paramITestResult);
        if (scenario != null) {
            scenario.getExecutor().failed(paramITestResult.getThrowable());
            testFinished(paramITestResult);
        }
    }

    @Override
    public void onTestSkipped(ITestResult testResult) {
    }

    private void testFinished(ITestResult testResult) {
        try {
            ScenarioBase scenario = getScenario(testResult);
            scenario.finished();
        } catch (FailIfPassedException ex) {
            testResult.setStatus(ITestResult.FAILURE);
            testResult.setThrowable(ex);
            testResult.getTestContext().getPassedTests().removeResult(testResult);
            testResult.getTestContext().getFailedTests().addResult(testResult);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        } finally {
            ScenarioHolder.get().removeScenarioOfCurrentThread();
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult paramITestResult) {
    }

    @Override
    public void onStart(ITestContext paramITestContext) {
        paramITestContext.setAttribute(REPORT_MODELS_ATTRIBUTE, new ConcurrentHashMap<String, ReportModel>());
    }

    @Override
    public void onFinish(ITestContext paramITestContext) {
        ConcurrentHashMap<String, ReportModel> reportModels = getReportModels(paramITestContext);
        for (ReportModel reportModel : reportModels.values()) {
            new CommonReportHelper().finishReport(reportModel);
        }
    }

    private ConcurrentHashMap<String, ReportModel> getReportModels(ITestContext paramITestContext) {
        return (ConcurrentHashMap<String, ReportModel>)
            paramITestContext.getAttribute(REPORT_MODELS_ATTRIBUTE);
    }

    private List<NamedArgument> getArgumentsFrom(Method method, ITestResult paramITestResult) {
        return ParameterNameUtil.mapArgumentsWithParameterNames(method, asList(paramITestResult.getParameters()));
    }
}
