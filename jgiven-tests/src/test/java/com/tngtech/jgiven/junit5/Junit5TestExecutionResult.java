package com.tngtech.jgiven.junit5;

import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.testframework.TestExecutionResult;

class Junit5TestExecutionResult extends TestExecutionResult {

    private final ReportModel report;
    private final org.junit.platform.engine.TestExecutionResult result;

    public Junit5TestExecutionResult(ReportModel report, org.junit.platform.engine.TestExecutionResult result) {
        this.report = report;
        this.result = result;
    }


    @Override
    public int getFailureCount() {
        return result.getStatus()
            == org.junit.platform.engine.TestExecutionResult.Status.FAILED ? 1 : 0;
    }

    @Override
    public String getFailureMessage(int i) {
        return result.getThrowable()
            .map(Throwable::getMessage)
            .orElseThrow(() -> new IndexOutOfBoundsException("No failure for index"));
    }

    @Override
    public ReportModel getReportModel() {
        return report;
    }
}
