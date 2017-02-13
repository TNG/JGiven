package com.tngtech.jgiven.report;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.report.ReportGenerator.Format;
import com.tngtech.jgiven.report.model.CompleteReportModel;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class WhenReportGenerator<SELF extends WhenReportGenerator<?>> extends Stage<SELF> {
    @ScenarioRule
    protected final TemporaryFolder temporaryFolderRule = new TemporaryFolder();

    @ExpectedScenarioState
    protected File jsonReportDirectory;

    @ExpectedScenarioState
    protected ReportGenerator reportGenerator;

    @ProvidedScenarioState
    protected File targetReportDir;

    @ProvidedScenarioState
    protected CompleteReportModel completeReportModel;

    @BeforeStage
    public void setupTargetReportDir() throws IOException {
        targetReportDir = temporaryFolderRule.newFolder( "targetReportDir" );
    }

    public void the_asciidoc_reporter_is_executed() {
        createReportGenerator();
        reportGenerator.addFlag( "--format=asciidoc" );
        reportGenerator.generate();
    }

    protected CompleteReportModel getCompleteReportModel() {
        return reportGenerator.createInternalReport( Format.HTML5 ).readReportModel();
    }

    protected void createReportGenerator() {
        reportGenerator.addFlag( "--sourceDir=" + jsonReportDirectory );
        reportGenerator.addFlag( "--targetDir=" + targetReportDir );
    }

    public void the_report_generator_is_executed() {
        createReportGenerator();
        reportGenerator.generate();
    }

    public SELF the_plain_text_reporter_is_executed() {
        createReportGenerator();
        reportGenerator.addFlag( "--format=text" );
        reportGenerator.generate();
        return self();
    }

    public SELF the_report_generator_is_executed_with_format( Format format ) {
        createReportGenerator();
        reportGenerator.addFlag( "--format=" + format );
        reportGenerator.generate();
        return self();
    }

    public SELF the_HTML5_report_has_been_generated() {
        return the_report_generator_is_executed_with_format( Format.HTML5 );
    }

    public SELF the_exclude_empty_scenarios_option_is_set_to( boolean excludeEmptyScenarios ) {
        reportGenerator.addFlag( "--exclude-empty-scenarios=" + excludeEmptyScenarios );
        return self();
    }

    public SELF reading_the_report_model() {
        createReportGenerator();
        completeReportModel = getCompleteReportModel();
        return self();
    }
}
