package com.tngtech.jgiven.junit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.tngtech.jgiven.report.impl.CommonReportHelper;
import com.tngtech.jgiven.report.model.ReportModel;

public class ScenarioReportRule extends TestWatcher {
    private ReportModel model;

    @Override
    protected void starting( Description description ) {
        model = new ReportModel();
    }

    @Override
    protected void finished( Description description ) {
        new CommonReportHelper().finishReport( model );
    }

    public ReportModel getTestCaseModel() {
        return model;
    }
}
