package com.tngtech.jgiven.report.html;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.report.model.GivenReportModel;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.tags.FeatureDataTables;
import com.tngtech.jgiven.tags.FeatureHtmlReport;
import com.tngtech.jgiven.tags.Issue;

@FeatureHtmlReport
@RunWith( DataProviderRunner.class )
public class HtmlWriterScenarioTest extends ScenarioTest<GivenReportModel<?>, WhenHtmlWriter, ThenHtmlOutput> {

    @DataProvider
    public static Object[][] statusTexts() {
        return new Object[][] {
            { StepStatus.PASSED, "something happens</li>" },
            { StepStatus.FAILED, "something happens <span class='badge failed'>failed</span></li>" },
            { StepStatus.SKIPPED, "something happens <span class='badge skipped'>skipped</span></li>" },
            { StepStatus.NOT_IMPLEMENTED_YET, "something happens <span class='badge notImplementedYet'>not implemented yet</span></li>" },
        };
    }

    @Test
    @UseDataProvider( "statusTexts" )
    public void step_status_appears_correctly_in_HTML_reports( StepStatus status, String expectedString ) {
        given().a_report_model_with_one_scenario()
            .and().step_$_is_named( 1, "something happens" )
            .and().step_$_has_status( 1, status );
        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_text( expectedString );
    }

    @Test
    @FeatureDataTables
    @Issue( "#9" )
    public void HTML_in_arguments_is_escaped_in_HTML_reports() {
        given().a_report_model_with_one_scenario()
            .and().case_$_has_a_when_step_$_with_argument( 1, "test", "<someHtmlTag>" );
        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_text( "&lt;someHtmlTag&gt;" );
    }

    @Test
    @FeatureDataTables
    public void data_tables_are_generated_in_HTML_reports() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_parameters( "param1", "param2" )
            .and().the_scenario_has_$_cases( 3 );
        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_a_data_table_with_header_values( "param1", "param2" )
            .and().the_data_table_has_one_line_for_the_arguments_of_each_case();
    }

    @Test
    @FeatureDataTables
    public void when_data_tables_are_generated_then_step_parameter_placeholders_are_correct_in_HTML_reports() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_$_cases( 2 )
            .and().the_scenario_has_parameters( "param1", "param2" )
            .and().case_$_has_arguments( 1, "a", "a" )
            .and().case_$_has_a_when_step_$_with_argument( 1, "uses the first parameter", "a" )
            .and().case_$_has_a_when_step_$_with_argument( 1, "uses the second parameter", "a" )
            .and().case_$_has_arguments( 2, "a", "b" )
            .and().case_$_has_a_when_step_$_with_argument( 2, "uses the first parameter", "a" )
            .and().case_$_has_a_when_step_$_with_argument( 2, "uses the second parameter", "b" );

        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_text( "uses the first parameter.*&lt;param1&gt;.*second" )
            .and().the_HTML_report_contains_text( "uses the second parameter.*&lt;param2&gt;.*Cases" )
            .and().the_HTML_report_contains_a_data_table_with_header_values( "param1", "param2" )
            .and().the_data_table_has_one_line_for_the_arguments_of_each_case();
    }

    @Test
    public void the_error_message_of_failed_scenarios_are_reported() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_$_cases( 1 )
            .and().case_$_fails_with_error_message( 1, "Test Error" );
        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_text( "<div class='failed'>Failed: Test Error</div>" );
    }
}
