package com.tngtech.jgiven.testframework;

import com.tngtech.jgiven.report.model.ReportModel;

public abstract class TestExecutionResult {

    public ReportModel reportModel;

    public abstract int getFailureCount();

    public abstract String getFailureMessage( int i );

    public ReportModel getReportModel() {
        return reportModel;
    }

}
