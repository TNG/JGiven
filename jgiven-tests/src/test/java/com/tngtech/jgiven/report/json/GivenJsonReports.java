package com.tngtech.jgiven.report.json;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.report.model.GivenReportModels;
import com.tngtech.jgiven.report.model.ReportModel;

public class GivenJsonReports<SELF extends GivenJsonReports<?>> extends GivenReportModels<SELF> {

    @ScenarioRule
    protected final TemporaryFolder temporaryFolderRule = new TemporaryFolder();

    @ProvidedScenarioState
    protected File jsonReportDirectory;

    @ProvidedScenarioState
    protected List<File> jsonReportFiles = Lists.newArrayList();

    @ProvidedScenarioState
    protected File customCssFile;

    public SELF a_report_model_as_JSON_file() throws IOException {
        a_report_model();
        return the_report_exist_as_JSON_file();
    }

    public SELF $_report_models_as_JSON_files( int n ) throws IOException {
        $_report_models( n );
        return the_report_exist_as_JSON_file();
    }

    public SELF the_report_exist_as_JSON_file() throws IOException {
        return the_reports_exist_as_JSON_files();
    }

    public SELF the_reports_exist_as_JSON_files() throws IOException {
        jsonReportDirectory = temporaryFolderRule.newFolder( "tmpJsonReports" );

        for( ReportModel reportModel : reportModels ) {
            File jsonReportFile = new File( jsonReportDirectory, reportModel.getClassName() + ".json" );

            jsonReportFiles.add( jsonReportFile );
            new ScenarioJsonWriter( reportModel ).write( jsonReportFile );
        }
        return self();
    }

    public SELF a_custom_CSS_file() throws IOException {
        customCssFile = temporaryFolderRule.newFile( "custom.css" );
        return self();
    }
}
