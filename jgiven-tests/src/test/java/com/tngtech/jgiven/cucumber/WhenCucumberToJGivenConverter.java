package com.tngtech.jgiven.cucumber;

import java.io.File;
import java.io.IOException;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.cucumber.json.CucumberJsonReport;

public class WhenCucumberToJGivenConverter extends Stage<WhenCucumberToJGivenConverter> {

    @ExpectedScenarioState
    File cucumberJsonReportFile;

    @ProvidedScenarioState
    CucumberJsonReport cucumberReport;

    public WhenCucumberToJGivenConverter the_file_can_be_parsed() throws IOException {
        cucumberReport = CucumberJsonReport.fromFile( cucumberJsonReportFile );
        return self();
    }
}
