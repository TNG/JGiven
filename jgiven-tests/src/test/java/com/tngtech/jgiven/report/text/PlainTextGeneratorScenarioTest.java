package com.tngtech.jgiven.report.text;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.report.WhenReportGenerator;
import com.tngtech.jgiven.report.json.GivenJsonReports;
import com.tngtech.jgiven.tags.FeatureTextReport;

@RunWith( DataProviderRunner.class )
@FeatureTextReport
public class PlainTextGeneratorScenarioTest extends
        ScenarioTest<GivenJsonReports<?>, WhenReportGenerator<?>, ThenPlainTextReportGenerator<?>> {

    @Test
    @DataProvider( { "0", "1", "3" } )
    public void the_plain_text_reporter_generates_one_file_for_each_test_class( int numberOfModels ) throws IOException {
        given().$_report_models( numberOfModels )
            .and().the_reports_exist_as_JSON_files();

        when().the_plain_text_reporter_is_executed();

        then().a_text_file_exists_for_each_test_class();
    }

}
