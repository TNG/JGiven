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
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportGenerator;
import com.tngtech.jgiven.report.json.ReportModelReader;
import com.tngtech.jgiven.report.model.CompleteReportModel;
import com.tngtech.jgiven.report.text.PlainTextReportGenerator;

public class WhenReportGenerator<SELF extends WhenReportGenerator<?>> extends Stage<SELF> {
    @ScenarioRule
    protected final TemporaryFolder temporaryFolderRule = new TemporaryFolder();

    @ExpectedScenarioState
    protected File jsonReportDirectory;

    @ExpectedScenarioState
    protected ReportGenerator.Config config;

    @ProvidedScenarioState
    protected File targetReportDir;

    @ProvidedScenarioState
    protected ReportGenerator reportGenerator;

    @ProvidedScenarioState
    protected CompleteReportModel completeReportModel;

    @BeforeStage
    public void setupTargetReportDir() throws IOException {
        targetReportDir = temporaryFolderRule.newFolder( "targetReportDir" );
    }

    public void the_asciidoc_reporter_is_executed() throws IOException {
        new AsciiDocReportGenerator().generate( getCompleteReportModel(), targetReportDir, config );
    }

    protected CompleteReportModel getCompleteReportModel() {
        return new ReportModelReader( config ).readDirectory( jsonReportDirectory );
    }

    private void createReportGenerator() {
        reportGenerator = new ReportGenerator();
        reportGenerator.setConfig( config );
        reportGenerator.setSourceDirectory( jsonReportDirectory );
        reportGenerator.setTargetDirectory( targetReportDir );
    }

    public void the_report_generator_is_executed() throws Exception {
        createReportGenerator();
        reportGenerator.generate();
    }

    public SELF the_plain_text_reporter_is_executed() {
        new PlainTextReportGenerator().generate( getCompleteReportModel(), targetReportDir, config );
        return self();
    }

    public SELF the_report_generator_is_executed_with_format( Format format ) throws Exception {
        createReportGenerator();
        reportGenerator.setFormat( format );
        reportGenerator.generate();
        return self();
    }

    public SELF the_HTML5_report_has_been_generated() throws Exception {
        return the_report_generator_is_executed_with_format( Format.HTML5 );
    }

    public SELF the_exclude_empty_scenarios_option_is_set_to( boolean excludeEmptyScenarios ) {
        config.setExcludeEmptyScenarios( excludeEmptyScenarios );
        return self();
    }

    public SELF reading_the_report_model() {
        createReportGenerator();
        completeReportModel = reportGenerator.readReportModel();
        return self();
    }
}
