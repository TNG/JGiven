package com.tngtech.jgiven.testframework;

import com.tngtech.jgiven.junit.JUnitExecutor;
import com.tngtech.jgiven.junit.ScenarioModelHolder;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.testng.TestNgExecutor;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

public abstract class TestExecutor {

    public static TestExecutor getExecutor(TestFramework framework) {
        switch (framework) {
            case JUnit:
                return new JUnitExecutor();
            case JUnit5:
                return new JUnit5Executor();
            case TestNG:
                return new TestNgExecutor();
            default:
                throw new IllegalArgumentException("Unknown framework: " + framework);
        }
    }

    public abstract TestExecutionResult execute(Class<?> testClass, String testMethod);

    public abstract TestExecutionResult execute(Class<?> testClass);

    static class JUnit5Executor extends TestExecutor {

        @Override
        public TestExecutionResult execute(Class<?> testClass, String testMethod) {
            Launcher launcher = LauncherFactory.create();
            LauncherDiscoveryRequest launcherRequest = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectMethod(testClass, testMethod)).build();
            TestPlan testPlan = launcher.discover(launcherRequest);
            TestExecutionListener listener = new TestExecutionResultProvider();
            launcher.registerTestExecutionListeners(listener);
            launcher.execute(testPlan);
            return null;
        }

        @Override
        public TestExecutionResult execute(Class<?> testClass) {
            Launcher launcher = LauncherFactory.create();
            LauncherDiscoveryRequest launcherRequest =
                LauncherDiscoveryRequestBuilder.request().selectors(DiscoverySelectors.selectClass(testClass)).build();
            TestPlan testPlan = launcher.discover(launcherRequest);
            TestExecutionListener listener = new TestExecutionResultProvider();
            launcher.registerTestExecutionListeners(listener);
            launcher.execute(testPlan);
            return null;
        }

        private static class TestExecutionResultProvider implements TestExecutionListener {

            ReportModel reportModel;
            org.junit.platform.engine.TestExecutionResult executionResult;


            @Override
            public void testPlanExecutionFinished(TestPlan testPlan) {
            }

            @Override
            public void executionSkipped(TestIdentifier testIdentifier, String reason) {
                TestExecutionListener.super.executionSkipped(testIdentifier, reason);
            }

            @Override
            public void executionStarted(TestIdentifier testIdentifier) {
                TestExecutionListener.super.executionStarted(testIdentifier);
            }

            @Override
            public void executionFinished(TestIdentifier testIdentifier,
                                          org.junit.platform.engine.TestExecutionResult testExecutionResult) {
                MethodSource source =
                    testIdentifier.getSource().filter(testSource -> testSource instanceof MethodSource)
                        .map(testSource -> (MethodSource) testSource).get();
                reportModel = ScenarioModelHolder.getInstance().getReportModel(source.getJavaClass());
                testExecutionResult = testExecutionResult;

            }

            @Override
            public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
                TestExecutionListener.super.reportingEntryPublished(testIdentifier, entry);
            }

            public TestExecutionResult getExecutionResult() {
                TestExecutionResult result = new TestExecutionResult() {

                    @Override
                    public int getFailureCount() {
                        return TestExecutionResultProvider.this.executionResult.getStatus()
                            == Status.FAILED ? 1 : 0;
                    }

                    @Override
                    public String getFailureMessage(int i) {
                        return TestExecutionResultProvider.this.executionResult.getThrowable()
                            .map(Throwable::getMessage)
                            .orElseThrow(() -> new IndexOutOfBoundsException("No failure for index"));
                    }
                };
                result.reportModel = reportModel;
                return result;
            }
        }
    }
}
