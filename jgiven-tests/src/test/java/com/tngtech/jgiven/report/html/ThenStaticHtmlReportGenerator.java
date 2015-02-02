package com.tngtech.jgiven.report.html;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.report.ThenReportGenerator;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioModel;

public class ThenStaticHtmlReportGenerator<SELF extends ThenStaticHtmlReportGenerator<?>> extends ThenReportGenerator<SELF> {

    @ExpectedScenarioState
    protected File customCssFile;

    public SELF an_index_file_exists() {
        return a_file_with_name_$_exists( "index.html" );
    }

    public SELF an_HTML_file_exists_for_each_test_class() {
        for( ReportModel model : reportModels ) {
            a_file_with_name_$_exists( model.getClassName() + ".html" );
        }
        return self();
    }

    public SELF the_custom_CSS_file_is_copied_to_the_target_directory() {
        assertThat( new File( targetReportDir, customCssFile.getName() ) ).exists();
        return self();
    }

    public SELF file_$_contains_scenario_$( String fileName, int scenarioNr ) throws IOException {
        final ScenarioModel scenarioModel = reportModels.get( 0 ).getScenarios().get( 0 );
        final String regex = ".*<div class='scenario-footer'>.*" + scenarioModel.getClassName() + "</a></div>.*";
        return file_$_contains_pattern(fileName, regex);
    }

}
