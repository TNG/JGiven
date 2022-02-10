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

    private final Map<Method, org.junit.platform.engine.TestExecutionResult> methodExecutionResults = new HashMap<>();
    private final Map<Class<?>, org.junit.platform.engine.TestExecutionResult> classExecutionResults = new HashMap<>();


    private ReportModel reportModel;

    @Override
    public void executionFinished(TestIdentifier testIdentifier,
                                  org.junit.platform.engine.TestExecutionResult testExecutionResult) {
        TestSource testSource = testIdentifier.getSource().orElse(null);
        if (testSource instanceof MethodSource) {
            handleTestMethodFinished((MethodSource) testSource, testExecutionResult);
        } else if (testSource instanceof ClassSource) {
            handleTestClassFinished((ClassSource) testSource, testExecutionResult);
        }
    }

    private void handleTestClassFinished(ClassSource testSource,
                                         org.junit.platform.engine.TestExecutionResult testExecutionResult) {
        classExecutionResults.put(testSource.getJavaClass(), testExecutionResult);
        setReportModel(testSource);
    }

    private void setReportModel(ClassSource testSource) {
        if (methodExecutionResults.size() == 0) {
            this.reportModel = null;
        } else {
            this.reportModel = JGivenReportExtractingExtension.getReportModelFor(testSource.getJavaClass())
                .orElseThrow(() -> new IllegalStateException("Failed to obtain report model for tested class."));
        }
    }

    private void handleTestMethodFinished(MethodSource testSource,
                                          org.junit.platform.engine.TestExecutionResult testExecutionResult) {
        methodExecutionResults.put(testSource.getJavaMethod(), testExecutionResult);
    }


    public TestExecutionResult getExecutionResult() {
        return new Junit5TestExecutionResult(reportModel, methodExecutionResults, classExecutionResults);
    }
}
