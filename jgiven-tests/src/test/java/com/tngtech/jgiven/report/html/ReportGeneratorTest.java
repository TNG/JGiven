package com.tngtech.jgiven.report.html;

import org.junit.Test;

import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.report.ReportGenerator;
import com.tngtech.jgiven.report.WhenReportGenerator;
import com.tngtech.jgiven.report.json.GivenJsonReports;
import com.tngtech.jgiven.report.text.ThenPlainTextReportGenerator;
import com.tngtech.jgiven.tags.FeatureHtmlReport;
import com.tngtech.jgiven.tags.FeatureTextReport;

public class ReportGeneratorTest extends JGivenScenarioTest<GivenJsonReports<?>, WhenReportGenerator<?>, ThenStaticHtmlReportGenerator<?>> {

    @Test
    @FeatureHtmlReport
    public void the_report_generator_can_generate_HTML_files() throws Exception {
        given().a_report_model()
                .and().the_reports_exist_as_JSON_files();

        given().a_custom_CSS_file();

        when().the_report_generator_is_executed_with_format(ReportGenerator.Format.HTML);

        then().an_index_file_exists()
                .and().the_custom_CSS_file_is_copied_to_the_target_directory()
                .and().an_HTML_file_exists_for_each_test_class();
    }

    @Test
    @FeatureTextReport
    public void the_report_generator_can_generate_text_files() throws Exception {
        ThenPlainTextReportGenerator<?> thenPlainText = addStage(ThenPlainTextReportGenerator.class);

        given().a_report_model()
                .and().the_reports_exist_as_JSON_files();

        when().the_report_generator_is_executed_with_format(ReportGenerator.Format.TEXT);

        thenPlainText.then()
                .a_text_file_exists_for_each_test_class();
    }
}
