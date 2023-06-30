package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.report.WhenReportGenerator;
import com.tngtech.jgiven.report.json.GivenJsonReports;
import com.tngtech.jgiven.report.model.GivenReportModels;
import com.tngtech.jgiven.tags.FeatureAsciiDocReport;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
@FeatureAsciiDocReport
public class AsciiDocReportGeneratorTest extends
    JGivenScenarioTest<GivenReportModels<?>, WhenReportGenerator<?>, ThenAsciiDocReportGenerator<?>> {

    @ScenarioStage
    private GivenJsonReports<?> jsonReports;

    @Test
    public void the_AsciiDoc_reporter_generates_an_index_file_a_test_file_and_multiple_other_asciidoc_files()
        throws IOException {
        given().a_report_model();
        jsonReports.and().the_report_exist_as_JSON_file();
        when().the_asciidoc_reporter_is_executed();
        then().a_file_with_name_$_exists("index.asciidoc")
            .and().a_file_with_name_$_exists("totalStatistics.asciidoc")
            .and().a_file_with_name_$_exists("allScenarios.asciidoc")
            .and().a_file_with_name_$_exists("failedScenarios.asciidoc")
            .and().a_file_with_name_$_exists("pendingScenarios.asciidoc")
            .and().a_file_with_name_$_exists("features/Test.asciidoc");
    }

    @Test
    public void the_multilines_values_are_rendered_as_literal_blocks() throws IOException {
        String content =
            "Some " + System.lineSeparator() + "text " + System.lineSeparator() + "with " + System.lineSeparator()
                + "newlines";
        given().a_report_model()
            .and().step_$_of_case_$_has_a_formatted_value_$_as_parameter(1, 1, content);
        jsonReports
            .and().the_report_exist_as_JSON_file();

        when().the_asciidoc_reporter_is_executed();
        then().the_asciidoc_report_$_exists("features/Test.asciidoc")
            .and().the_literal_block_is_added_$(
                "...." + System.lineSeparator()
                    + content + System.lineSeparator()
                    + "....");
    }

}
