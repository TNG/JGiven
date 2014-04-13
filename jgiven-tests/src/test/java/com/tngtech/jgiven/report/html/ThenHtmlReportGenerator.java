package com.tngtech.jgiven.report.html;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.report.model.ReportModel;

public class ThenHtmlReportGenerator extends Stage<ThenHtmlReportGenerator> {
    @ExpectedScenarioState
    private File targetReportDir;

    @ExpectedScenarioState
    protected List<ReportModel> reportModels;

    public ThenHtmlReportGenerator an_index_file_exists() {
        return a_file_with_name_$_exists( "index.html" );
    }

    public ThenHtmlReportGenerator an_HTML_file_exists_for_each_test_class() {
        for( ReportModel model : reportModels ) {
            a_file_with_name_$_exists( model.className + ".html" );
        }
        return self();
    }

    public ThenHtmlReportGenerator a_file_with_name_$_exists( String name ) {
        assertThat( new File( targetReportDir, name ) ).exists();
        return self();
    }

}
