package com.tngtech.jgiven.junit5;

import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.testframework.TestExecutionResult;
import java.lang.reflect.Method;
import java.util.Map;

class Junit5TestExecutionResult extends TestExecutionResult {

    private final ReportModel report;
    private final Map<Method, org.junit.platform.engine.TestExecutionResult> result;

    public Junit5TestExecutionResult(ReportModel report,
                                     Map<Method, org.junit.platform.engine.TestExecutionResult> result) {
        this.report = report;
        this.result = result;
    }


    @Override
    public int getFailureCount() {
        return (int) result.values().stream()
            .filter(result -> result.getStatus() == org.junit.platform.engine.TestExecutionResult.Status.FAILED)
            .count();
    }

    @Override
    public String getFailureMessage(int i) {
        if (result.size() == 1 && i == 0) {
            return result.values().stream()
                .findFirst()
                .flatMap(org.junit.platform.engine.TestExecutionResult::getThrowable)
                .map(Throwable::getMessage)
                .orElseThrow(() -> new IllegalStateException("Expected failure message for test 1, but none present."));

        } else {
            throw new UnsupportedOperationException("Cannot address a map by Index."
                + "This method is a result of attempting to adapt to data model that was focused on JUnit4. "
                + "Please refactor.");
        }
    }

    @Override
    public ReportModel getReportModel() {
        return report;
    }
}
