package com.tngtech.jgiven.testframework;

import com.tngtech.jgiven.JGivenReportExtractingExtension;
import com.tngtech.jgiven.junit.JUnitExecutor;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.testng.TestNgExecutor;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
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
            LauncherDiscoveryRequest launcherRequest = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectMethod(testClass, testMethod))
                .build();
            return run(launcherRequest);
        }

        @Override
        public TestExecutionResult execute(Class<?> testClass) {
            LauncherDiscoveryRequest launcherRequest =
                LauncherDiscoveryRequestBuilder.request().selectors(DiscoverySelectors.selectClass(testClass)).build();
            return run(launcherRequest);
        }

        public TestExecutionResult run(LauncherDiscoveryRequest launchRequest) {
            TestExecutionResultProvider listener = new TestExecutionResultProvider();

            try (LauncherSession session = LauncherFactory.openSession()) {
                Launcher launcher = session.getLauncher();
                launcher.registerTestExecutionListeners(listener);
                launcher.execute(launchRequest);
                return listener.getExecutionResult();
            }
        }

        private static class TestExecutionResultProvider implements TestExecutionListener {

            ReportModel reportModel;
            org.junit.platform.engine.TestExecutionResult executionResult;


            @Override
            public void executionFinished(TestIdentifier testIdentifier,
                                          org.junit.platform.engine.TestExecutionResult testExecutionResult) {
                //TODO: context operates on Method unique Id (at least for single method executions) but testIdentifier is a class UniqueId
                reportModel = JGivenReportExtractingExtension.getReportModelFor(testIdentifier.getUniqueId()).get();
                executionResult = testExecutionResult;

            }

            //TODO make this object more independent from its parent
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
