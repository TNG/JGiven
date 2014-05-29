package com.tngtech.jgiven.junit;

import java.util.Stack;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.tngtech.jgiven.report.impl.CommonReportHelper;
import com.tngtech.jgiven.report.model.ReportModel;

public class ScenarioReportRule extends TestWatcher {
    private final Stack<ReportModel> models = new Stack<ReportModel>();

    @Override
    protected void starting( Description description ) {
        models.push( new ReportModel() );
    }

    @Override
    protected void finished( Description description ) {
        new CommonReportHelper().finishReport( models.pop() );
    }

    public ReportModel getTestCaseModel() {
        return models.peek();
    }
}
