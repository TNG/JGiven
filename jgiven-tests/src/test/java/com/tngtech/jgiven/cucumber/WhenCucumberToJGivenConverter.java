package com.tngtech.jgiven.cucumber;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.cucumber.json.CucumberJsonReport;
import com.tngtech.jgiven.report.model.ReportModel;

public class WhenCucumberToJGivenConverter extends Stage<WhenCucumberToJGivenConverter> {

    @ExpectedScenarioState
    File cucumberJsonReportFile;

    @ProvidedScenarioState
    CucumberJsonReport cucumberReport;

    @ProvidedScenarioState
    private List<ReportModel> reportModels;

    public WhenCucumberToJGivenConverter the_file_can_be_parsed() throws IOException {
        cucumberReport = CucumberJsonReport.fromFile( cucumberJsonReportFile );
        return self();
    }

    public WhenCucumberToJGivenConverter it_is_converted_to_a_JGiven_report() {
        reportModels = new CucumberToJGivenConverter().convert( cucumberReport );
        return self();
    }
}
