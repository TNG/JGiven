package com.tngtech.jgiven.cucumber;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;
import com.tngtech.jgiven.cucumber.json.CucumberFeature;
import com.tngtech.jgiven.cucumber.json.CucumberJsonReport;
import com.tngtech.jgiven.cucumber.json.CucumberScenario;
import com.tngtech.jgiven.cucumber.json.CucumberStep;

public class GivenCucumberReport extends Stage<GivenCucumberReport> {

    @ProvidedScenarioState
    File cucumberJsonReportFile;

    @ExpectedScenarioState
    CurrentStep currentStep;

    @ProvidedScenarioState
    CucumberJsonReport cucumberJsonReport;

    @ProvidedScenarioState
    CucumberFeature cucumberFeature;

    @ProvidedScenarioState
    CucumberScenario cucumberScenario;

    @ProvidedScenarioState
    private CucumberStep cucumberStep;

    @ExtendedDescription( "An example Cucumber report in JSON format. See attachment for the concrete content." )
    public GivenCucumberReport a_Cucumber_report_as_JSON_file() throws IOException {
        File root = getRootDir();

        cucumberJsonReportFile = new File( root, "src/test/resources/com/tngtech/jgiven/cucumber/cucumber-report.json" );
        currentStep.addAttachment(
            Attachment.fromTextFile( cucumberJsonReportFile, MediaType.PLAIN_TEXT, Charsets.UTF_8 )
                .withTitle( "The JSON file" ) );
        return self();
    }

    private File getRootDir() {
        // depending on where the test is execute the root folder might be different
        File root = new File( "." );
        if( new File( root, "jgiven-tests" ).exists() ) {
            root = new File( "jgiven-tests" );
        }
        return root;
    }

    public GivenCucumberReport a_Cucumber_report() {
        cucumberJsonReport = new CucumberJsonReport();
        a_feature();
        return self();
    }

    public GivenCucumberReport a_feature() {
        cucumberFeature = new CucumberFeature();
        cucumberJsonReport.features.add( cucumberFeature );
        a_scenario();
        return self();
    }

    public GivenCucumberReport a_scenario() {
        cucumberScenario = new CucumberScenario();
        cucumberFeature.elements.add( cucumberScenario );
        a_given_step();
        a_when_step();
        a_then_step();
        return self();
    }

    private GivenCucumberReport a_then_step() {
        a_step();
        return self();
    }

    private GivenCucumberReport a_step() {
        cucumberStep = new CucumberStep();
        return self();
    }

    public GivenCucumberReport a_when_step() {
        a_step();
        return self();
    }

    public GivenCucumberReport a_given_step() {
        a_step();
        return self();
    }

}
