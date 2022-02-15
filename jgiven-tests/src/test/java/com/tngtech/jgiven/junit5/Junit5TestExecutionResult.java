package com.tngtech.jgiven.junit5;

import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.testframework.TestExecutionResult;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

class Junit5TestExecutionResult extends TestExecutionResult {

    private final ReportModel report;
    private final Map<Method, org.junit.platform.engine.TestExecutionResult> methodResults;
    private final Map<Class<?>, org.junit.platform.engine.TestExecutionResult> classResults;

    public Junit5TestExecutionResult(ReportModel report,
                                     Map<Method, org.junit.platform.engine.TestExecutionResult> result,
                                     Map<Class<?>, org.junit.platform.engine.TestExecutionResult> classResults) {
        this.report = report;
        this.methodResults = result;
        this.classResults = classResults;
    }


    @Override
    public ReportModel getReportModel() {
        return report;
    }

    @Override
    public int getFailureCount() {
        return (int) Stream.concat(methodResults.values().stream(), classResults.values().stream())
            .filter(result -> result.getStatus() == org.junit.platform.engine.TestExecutionResult.Status.FAILED)
            .count();
    }

    @Override
    public String getFailureMessage(int i) {
        if (i == 0 && getFailureCount() == 1) {
            return assumeCallerRequestsTheOnlyFailureThatExists();
        } else {
            throw new UnsupportedOperationException("Cannot address a map by Index."
                + "This method is a result of attempting to adapt to data model that was focused on JUnit4. "
                + "Please refactor.");
        }
    }

    private String assumeCallerRequestsTheOnlyFailureThatExists() {
        if (noFailureInClassResults()) {
            return getFailureMessageFromTestResults(methodResults);
        } else {
            return getFailureMessageFromTestResults(classResults);
        }
    }

    private boolean noFailureInClassResults() {
        return classResults.values().stream()
            .map(org.junit.platform.engine.TestExecutionResult::getStatus)
            .allMatch(status -> status == org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL);
    }

    private String getFailureMessageFromTestResults(Map<?, org.junit.platform.engine.TestExecutionResult> results) {
        return results.values().stream()
            .findFirst()
            .flatMap(org.junit.platform.engine.TestExecutionResult::getThrowable)
            .map(Throwable::getMessage)
            .orElseThrow(() -> new IllegalStateException("Expected failure message for test 1, but none present."));
    }
}
