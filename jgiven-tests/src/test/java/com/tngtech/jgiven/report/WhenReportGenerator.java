package com.tngtech.jgiven.report;

import java.io.File;
import java.io.IOException;

import org.junit.rules.TemporaryFolder;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.report.ReportGenerator.Format;
import com.tngtech.jgiven.report.html.StaticHtmlReportGenerator;
import com.tngtech.jgiven.report.text.PlainTextReportGenerator;

public class WhenReportGenerator<SELF extends WhenReportGenerator<?>> extends Stage<SELF> {
    @ScenarioRule
    protected final TemporaryFolder temporaryFolderRule = new TemporaryFolder();

    @ProvidedScenarioState
    protected File targetReportDir;

    @ExpectedScenarioState
    protected File jsonReportDirectory;

    @ExpectedScenarioState
    protected File customCssFile;

    @ProvidedScenarioState
    protected ReportGenerator htmlReportGenerator;

    @BeforeStage
    protected void setupTargetReportDir() throws IOException {
        targetReportDir = temporaryFolderRule.newFolder( "targetReportDir" );
    }

    public void the_static_HTML_reporter_is_executed() throws IOException {
        new StaticHtmlReportGenerator().generate( targetReportDir, jsonReportDirectory );
    }

    private void createReportGenerator() {
        htmlReportGenerator = new ReportGenerator();
        htmlReportGenerator.setCustomCssFile( customCssFile );
        htmlReportGenerator.setSourceDir( jsonReportDirectory );
        htmlReportGenerator.setToDir( targetReportDir );
    }

    public void the_report_generator_is_executed() throws IOException {
        createReportGenerator();
        htmlReportGenerator.generate();
    }

    public SELF the_plain_text_reporter_is_executed() {
        new PlainTextReportGenerator().generate( targetReportDir, jsonReportDirectory );
        return self();
    }

    public SELF the_report_generator_is_executed_with_format( Format format ) throws IOException {
        createReportGenerator();
        htmlReportGenerator.setFormat( format );
        htmlReportGenerator.generate();
        return self();
    }
}
