package com.tngtech.jgiven.junit5;

import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.testframework.TestExecutionResult;
import com.tngtech.jgiven.tests.JGivenReportExtractingExtension;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

class TestExecutionResultProvider implements TestExecutionListener {

    private Map<Method, org.junit.platform.engine.TestExecutionResult> executionResults = new HashMap<>();


    private ReportModel reportModel;

    @Override
    public void executionFinished(TestIdentifier testIdentifier,
                                  org.junit.platform.engine.TestExecutionResult testExecutionResult) {
        TestSource testSource = testIdentifier.getSource().orElse(null);
        if (testSource instanceof MethodSource) {
            handleTestMethodFinished((MethodSource) testSource, testExecutionResult);
        } else if (testSource instanceof ClassSource) {
            handleTestClassFinished((ClassSource) testSource);
        }
    }

    private void handleTestClassFinished(ClassSource testSource) {
        this.reportModel = JGivenReportExtractingExtension.getReportModelFor(testSource.getJavaClass())
            .orElseThrow(() -> new IllegalStateException("Failed to obtain report model for tested class."));
    }

    private void handleTestMethodFinished(MethodSource testSource,
                                          org.junit.platform.engine.TestExecutionResult testExecutionResult) {
        executionResults.put(testSource.getJavaMethod(), testExecutionResult);
    }


    public TestExecutionResult getExecutionResult() {
        return new Junit5TestExecutionResult(reportModel, executionResults);
    }
}
