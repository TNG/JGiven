package com.tngtech.jgiven.report.json;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import org.junit.rules.TemporaryFolder;

import com.tngtech.jgiven.relocated.guava.base.Charsets;
import com.tngtech.jgiven.relocated.guava.collect.Lists;
import com.tngtech.jgiven.relocated.guava.io.Files;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.report.ReportGenerator;
import com.tngtech.jgiven.report.analysis.CaseArgumentAnalyser;
import com.tngtech.jgiven.report.model.GivenReportModels;
import com.tngtech.jgiven.report.model.ReportModel;

public class GivenJsonReports<SELF extends GivenJsonReports<?>> extends Stage<SELF> {

    @ScenarioRule
    protected final TemporaryFolder temporaryFolderRule = new TemporaryFolder();

    @ExpectedScenarioState
    protected List<ReportModel> reportModels = Lists.newArrayList();

    @ExpectedScenarioState
    protected ReportModel reportModel;

    @ProvidedScenarioState
    protected File jsonReportDirectory;

    @ProvidedScenarioState
    protected List<File> jsonReportFiles = Lists.newArrayList();

    @ProvidedScenarioState
    protected ReportGenerator.Config config = new ReportGenerator.Config();


    public SELF the_report_exist_as_JSON_file() throws IOException {
        if (reportModel != null) {
            reportModels.add(reportModel);
        }
        return the_reports_exist_as_JSON_files();
    }

    public SELF the_reports_exist_as_JSON_files() throws IOException {
        jsonReportDirectory = temporaryFolderRule.newFolder( "tmpJsonReports" );

        for( ReportModel reportModel : reportModels ) {
            new CaseArgumentAnalyser().analyze( reportModel );
            File jsonReportFile = new File( jsonReportDirectory, reportModel.getClassName() + ".json" );

            jsonReportFiles.add( jsonReportFile );
            new ScenarioJsonWriter( reportModel ).write( jsonReportFile );
        }
        return self();
    }

    public SELF a_custom_CSS_file() throws IOException {
        config.setCustomCssFile( temporaryFolderRule.newFile( "custom.css" ) );
        return self();
    }

    public SELF a_custom_JS_file_with_content( String content ) throws IOException {
        config.setCustomJsFile( temporaryFolderRule.newFile( "custom.js" ) );
        Files.append( content, config.getCustomJsFile(), Charsets.UTF_8 );
        return self();
    }
}
