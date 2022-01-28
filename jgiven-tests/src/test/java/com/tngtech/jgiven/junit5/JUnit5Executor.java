package com.tngtech.jgiven.junit5;

import com.tngtech.jgiven.testframework.TestExecutionResult;
import com.tngtech.jgiven.testframework.TestExecutor;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

public class JUnit5Executor extends TestExecutor {

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

    private TestExecutionResult run(LauncherDiscoveryRequest launchRequest) {
        TestExecutionResultProvider listener = new TestExecutionResultProvider();

        try (LauncherSession session = LauncherFactory.openSession()) {
            Launcher launcher = session.getLauncher();
            launcher.registerTestExecutionListeners(listener);
            launcher.execute(launchRequest);
            return listener.getExecutionResult();
        }
    }
}
