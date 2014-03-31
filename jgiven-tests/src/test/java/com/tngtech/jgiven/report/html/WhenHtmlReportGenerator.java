package com.tngtech.jgiven.report.html;

import java.io.File;
import java.io.IOException;

import org.junit.rules.TemporaryFolder;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioRule;

public class WhenHtmlReportGenerator extends Stage<WhenHtmlReportGenerator> {
    @ScenarioRule
    protected final TemporaryFolder temporaryFolderRule = new TemporaryFolder();

    @ProvidedScenarioState
    protected File targetReportDir;

    @ExpectedScenarioState
    protected File jsonReportDirectory;

    @BeforeStage
    protected void setupTargetReportDir() throws IOException {
        targetReportDir = temporaryFolderRule.newFolder();
    }

    public void the_frame_based_HTML_reporter_is_executed() throws IOException {
        new FrameBasedHtmlReportGenerator().generate( targetReportDir, jsonReportDirectory );
    }

}
