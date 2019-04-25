package com.tngtech.jgiven.report;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.report.ReportGenerator.Format;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportConfig;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportGenerator;
import com.tngtech.jgiven.report.html5.Html5ReportConfig;
import com.tngtech.jgiven.report.model.CompleteReportModel;
import com.tngtech.jgiven.report.text.PlainTextReportConfig;
import com.tngtech.jgiven.report.text.PlainTextReportGenerator;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class WhenReportGenerator<SELF extends WhenReportGenerator<?>> extends Stage<SELF> {
    @ScenarioRule
    protected final TemporaryFolder temporaryFolderRule = new TemporaryFolder();

    @ExpectedScenarioState
    protected File jsonReportDirectory;

    @ExpectedScenarioState
    protected AsciiDocReportConfig asciiDocReportConfig;

    @ExpectedScenarioState
    protected PlainTextReportConfig plainTextReportConfig;

    @ExpectedScenarioState
    protected Html5ReportConfig html5ReportConfig;

    @ProvidedScenarioState
    protected File targetReportDir;

    @ProvidedScenarioState
    protected CompleteReportModel completeReportModel;

    @BeforeStage
    public void setupTargetReportDir() throws IOException {
        targetReportDir = temporaryFolderRule.newFolder( "targetReportDir" );
    }

    protected CompleteReportModel getCompleteReportModel() {
        return asciiDocReportConfig.getReportModel();
    }

    protected void setupReportConfig() {
        asciiDocReportConfig.setSourceDir( jsonReportDirectory );
        asciiDocReportConfig.setTargetDir( targetReportDir );

        plainTextReportConfig.setSourceDir( jsonReportDirectory );
        plainTextReportConfig.setTargetDir( targetReportDir );

        html5ReportConfig.setSourceDir( jsonReportDirectory );
        html5ReportConfig.setTargetDir( targetReportDir );
    }

    public void the_report_generator_is_executed() {
        the_report_generator_is_executed_with_format( Format.HTML5 );
    }

    public SELF the_asciidoc_reporter_is_executed() {
        return the_report_generator_is_executed_with_format( Format.ASCIIDOC );
    }

    public SELF the_plain_text_reporter_is_executed() {
        return the_report_generator_is_executed_with_format( Format.TEXT );
    }

    public SELF the_HTML5_reporter_is_executed() {
        return the_report_generator_is_executed_with_format( Format.HTML5 );
    }

    public SELF the_report_generator_is_executed_with_format( Format format ) {
        setupReportConfig();
        switch( format ) {
            case ASCIIDOC:
                new AsciiDocReportGenerator().generateWithConfig( asciiDocReportConfig );
                break;
            case TEXT:
                new PlainTextReportGenerator().generateWithConfig( plainTextReportConfig );
                break;
            case HTML:
            case HTML5:
            default:
                ReportGenerator.generateHtml5Report().generateWithConfig( html5ReportConfig );
        }
        return self();
    }

    public SELF the_exclude_empty_scenarios_option_is_set_to( boolean excludeEmptyScenarios ) {
        asciiDocReportConfig.setExcludeEmptyScenarios( excludeEmptyScenarios );
        plainTextReportConfig.setExcludeEmptyScenarios( excludeEmptyScenarios );
        html5ReportConfig.setExcludeEmptyScenarios( excludeEmptyScenarios );
        return self();
    }

    public SELF reading_the_report_model() {
        setupReportConfig();
        completeReportModel = getCompleteReportModel();
        return self();
    }
}
