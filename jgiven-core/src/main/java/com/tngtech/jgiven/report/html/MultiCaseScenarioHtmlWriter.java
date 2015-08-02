package com.tngtech.jgiven.report.html;

import java.io.PrintWriter;

import com.tngtech.jgiven.report.model.ReportModel;

public class MultiCaseScenarioHtmlWriter extends ScenarioHtmlWriter {

    public MultiCaseScenarioHtmlWriter( PrintWriter writer, ReportModel reportModel ) {
        super( writer, reportModel );
    }

}
