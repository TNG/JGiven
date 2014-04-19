package com.tngtech.jgiven.report.html;

import java.io.IOException;

import org.junit.Test;

import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.report.json.GivenJsonReports;
import com.tngtech.jgiven.tags.FeatureHtmlReport;

@FeatureHtmlReport
public class HtmlReportGeneratorTest extends ScenarioTest<GivenJsonReports<?>, WhenHtmlReportGenerator, ThenHtmlReportGenerator> {

    @Test
    @FeatureHtmlReport
    public void the_HTML_report_generator_works_as_expected() throws IOException {
        given().a_report_model()
            .and().the_reports_exist_as_JSON_files();

        given().a_custom_CSS_file();

        when().the_HTML_report_generator_is_executed();

        then().an_index_file_exists()
            .and().the_custom_CSS_file_is_copied_to_the_target_directory()
            .and().an_HTML_file_exists_for_each_test_class();
    }

}
