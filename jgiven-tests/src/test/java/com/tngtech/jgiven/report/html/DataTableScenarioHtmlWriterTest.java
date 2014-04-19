package com.tngtech.jgiven.report.html;

import org.junit.Test;

import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.report.model.GivenReportModel;
import com.tngtech.jgiven.tags.FeatureDataTables;
import com.tngtech.jgiven.tags.FeatureHtmlReport;

@FeatureDataTables
@FeatureHtmlReport
public class DataTableScenarioHtmlWriterTest extends ScenarioTest<GivenReportModel<?>, WhenHtmlWriter, ThenHtmlOutput> {

    @Test
    public void data_tables_are_generated_in_HTML_reports() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_is_annotated_with_CasesAsTables()
            .and().the_scenario_has_parameters( "param1", "param2" )
            .and().the_scenario_has_$_cases( 3 );
        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_a_data_table_with_header_values( "param1", "param2" )
            .and().the_data_table_has_one_line_for_the_arguments_of_each_case();
    }
}
