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
        assertThat( new File( targetReportDir, "index.html" ) ).exists();
        return self();
    }

    public ThenHtmlReportGenerator an_HTML_file_exists_for_each_test_class() {
        for( ReportModel model : reportModels ) {
            assertThat( new File( targetReportDir, model.className + ".html" ) ).exists();
        }
        return self();
    }

}
