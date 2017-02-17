package com.tngtech.jgiven.report.json;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.report.AbstractReportConfig;
import com.tngtech.jgiven.report.asciidoc.AsciiDocReportConfig;
import com.tngtech.jgiven.report.html5.Html5ReportConfig;
import com.tngtech.jgiven.report.text.PlainTextReportConfig;
import org.apache.tools.ant.taskdefs.Javadoc;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.report.ReportGenerator;
import com.tngtech.jgiven.report.analysis.CaseArgumentAnalyser;
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
    protected AsciiDocReportConfig asciiDocReportConfig = new AsciiDocReportConfig();

    @ProvidedScenarioState
    protected PlainTextReportConfig plainTextReportConfig = new PlainTextReportConfig();

    @ProvidedScenarioState
    protected Html5ReportConfig html5ReportConfig = new Html5ReportConfig();

    public SELF the_report_exist_as_JSON_file() throws IOException {
        if( reportModel != null ) {
            reportModels.add( reportModel );
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
        File cssFile = temporaryFolderRule.newFile( "custom.css" );
        html5ReportConfig.setCustomCss( cssFile );
        return self();
    }

    public SELF a_custom_JS_file_with_content( String content ) throws IOException {
        File jsFile = temporaryFolderRule.newFile( "custom.js" );
        html5ReportConfig.setCustomJs( jsFile );
        Files.append( content, jsFile, Charsets.UTF_8 );
        return self();
    }
}
