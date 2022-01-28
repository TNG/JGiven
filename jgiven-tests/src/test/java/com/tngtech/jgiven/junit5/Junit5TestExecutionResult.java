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
        if (i >= result.size()) {
            throw new IndexOutOfBoundsException("No result for index");
        } else if (result.size() == 1 && i == 0) {
            return result.values().stream()
                .findFirst()
                .flatMap(org.junit.platform.engine.TestExecutionResult::getThrowable)
                .map(Throwable::getMessage)
                .orElse("");

        } else {
            throw new UnsupportedOperationException("cannot index map");
        }
    }

    @Override
    public ReportModel getReportModel() {
        return report;
    }
}
