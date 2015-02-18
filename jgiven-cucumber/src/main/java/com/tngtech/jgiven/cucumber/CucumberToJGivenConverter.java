package com.tngtech.jgiven.cucumber;

import java.io.File;
import java.io.IOException;

import com.tngtech.jgiven.cucumber.json.CucumberJsonReport;
import com.tngtech.jgiven.report.model.ReportModel;

public class CucumberToJGivenConverter {

    public void convert( File cucumberJsonDir, File jgivenJsonTargetDir ) {}

    public ReportModel convert( File cucumberJsonReportFile ) throws IOException {
        return convert( CucumberJsonReport.fromFile( cucumberJsonReportFile ) );
    }

    public ReportModel convert( CucumberJsonReport cucumberJsonReport ) {
        return null;
    }
}
