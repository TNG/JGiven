package com.tngtech.jgiven.report.html;

import static java.util.Arrays.asList;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.report.model.GivenReportModel;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.tags.*;

@FeatureHtmlReport
@RunWith( DataProviderRunner.class )
public class ReportModelHtmlWriterScenarioTest extends JGivenScenarioTest<GivenReportModel<?>, WhenReportModelHtmlWriter, ThenHtmlOutput> {

    @DataProvider
    public static Object[][] statusTexts() {
        return new Object[][] {
            { StepStatus.PASSED, "something happens.*</li>" },
            { StepStatus.FAILED, "something happens</span> <span class='badge failed'>failed</span>.*</li>" },
            { StepStatus.SKIPPED, "something happens</span> <span class='badge skipped'>skipped</span>.*</li>" },
            { StepStatus.NOT_IMPLEMENTED_YET,
                "something happens</span> <span class='badge notImplementedYet'>not implemented yet</span>.*</li>" },
        };
    }

    @Test
    @UseDataProvider( "statusTexts" )
    public void step_status_appears_correctly_in_HTML_reports( StepStatus status, String expectedString ) {
        given().a_report_model_with_one_scenario()
            .and().step_$_is_named( 1, "something happens" )
            .and().step_$_has_status( 1, status );
        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_pattern( expectedString );
    }

    @Test
    @Issue( "#9" )
    public void HTML_in_arguments_is_escaped_in_HTML_reports() {
        given().a_report_model_with_one_scenario()
            .and().case_$_has_a_when_step_$_with_argument( 1, "test", "<someHtmlTag>" );
        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_pattern( "&lt;someHtmlTag&gt;" );
    }

    @Test
    @FeatureDataTables
    public void step_arguments_matching_case_arguments_are_replaced_by_scenario_parameter_names() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_parameters( "param1", "param2" )
            .and().the_scenario_has_$_cases( 2 )
            .and().case_$_has_arguments( 1, "a", "b" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "uses the first parameter", "a", "stepArg1" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "uses the second parameter", "b", "stepArg2" )
            .and().case_$_has_arguments( 2, "c", "d" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "uses the first parameter", "c", "stepArg1" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "uses the second parameter", "d", "stepArg2" );

        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_pattern( "uses the first parameter.*&lt;param1&gt;.*second" )
            .and().the_HTML_report_contains_pattern( "uses the second parameter.*&lt;param2&gt;.*Cases" )
            .and().the_HTML_report_contains_a_data_table_with_header_values( "param1", "param2" )
            .and().line_$_of_the_data_table_has_arguments_$( 1, "a", "b" )
            .and().line_$_of_the_data_table_has_arguments_$( 2, "c", "d" );
    }

    @Test
    @FeatureDataTables
    public void scenario_parameters_and_step_arguments_can_be_mixed() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_parameters( "param1" )
            .and().the_scenario_has_$_cases( 2 )
            .and().case_$_has_arguments( 1, "a" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "uses the first parameter", "a", "stepArg1" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "uses the second parameter", "b", "stepArg2" )
            .and().case_$_has_arguments( 2, "c" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "uses the first parameter", "c", "stepArg1" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "uses the second parameter", "d", "stepArg2" );

        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_pattern( "uses the first parameter.*&lt;param1&gt;.*second" )
            .and().the_HTML_report_contains_pattern( "uses the second parameter.*&lt;stepArg2&gt;.*Cases" )
            .and().the_HTML_report_contains_a_data_table_with_header_values( "param1", "stepArg2" )
            .and().line_$_of_the_data_table_has_arguments_$( 1, "a", "b" )
            .and().line_$_of_the_data_table_has_arguments_$( 2, "c", "d" );
    }

    @Test
    @FeatureDataTables
    public void derived_parameters_with_the_same_name_should_be_avoided() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_$_cases( 2 )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "step1", "a", "stepArg" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "step2", "b", "stepArg" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "step1", "c", "stepArg" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "step2", "d", "stepArg" );

        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_pattern( "step1.*&lt;stepArg&gt;.*step2" )
            .and().the_HTML_report_contains_pattern( "step2.*&lt;stepArg2&gt;.*Cases" )
            .and().the_HTML_report_contains_a_data_table_with_header_values( "stepArg", "stepArg2" )
            .and().line_$_of_the_data_table_has_arguments_$( 1, "a", "b" )
            .and().line_$_of_the_data_table_has_arguments_$( 2, "c", "d" );
    }

    @Test
    @FeatureDataTables
    @FeatureDerivedParameters
    public void when_data_tables_are_generated_then_step_parameter_placeholders_are_correct_in_HTML_reports() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_parameters( "param1", "param2" )
            .and().the_scenario_has_$_cases( 2 )
            .and().case_$_has_arguments( 1, "x", "y" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "uses the first parameter", "a", "stepArg1" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "uses the second parameter", "b", "stepArg2" )
            .and().case_$_has_arguments( 2, "x2", "y2" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "uses the first parameter", "c", "stepArg1" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "uses the second parameter", "d", "stepArg2" );

        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_pattern( "uses the first parameter.*&lt;stepArg1&gt;.*second" )
            .and().the_HTML_report_contains_pattern( "uses the second parameter.*&lt;stepArg2&gt;.*Cases" )
            .and().the_HTML_report_contains_a_data_table_with_header_values( "stepArg1", "stepArg2" )
            .and().line_$_of_the_data_table_has_arguments_$( 1, "a", "b" )
            .and().line_$_of_the_data_table_has_arguments_$( 2, "c", "d" );
    }

    @Test
    public void when_cases_are_structurally_different_then_each_case_appears_seperately() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_$_default_cases( 2 )
            .and().the_scenario_has_parameters( "param1", "param2" )
            .and().case_$_has_arguments( 1, "arg1", "anotherArg1" )
            .and().case_$_has_a_step_$_with_argument( 1, "uses the first parameter", "arg1" )
            .and().case_$_has_arguments( 2, "arg2", "anotherArg2" )
            .and().case_$_has_a_step_$_with_argument( 2, "uses the first parameter", "arg2" )
            .and().case_$_has_step_$( 2, "some extra step method only appearing in case 2" );

        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_pattern( "Case 1: param1 = arg1, param2 = anotherArg1" )
            .and().the_HTML_report_contains_pattern( "uses the first parameter.*arg1.*Case 2" )
            .and().the_HTML_report_contains_pattern( "Case 2: param1 = arg2, param2 = anotherArg2" )
            .and().the_HTML_report_contains_pattern( "uses the first parameter.*arg2.*" )
            .and().the_HTML_report_contains_pattern( "some extra step method only appearing in case 2" );
    }

    @Test
    public void the_error_message_of_failed_scenarios_are_reported() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_$_default_cases( 1 )
            .and().case_$_fails_with_error_message( 1, "Test Error" );
        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_text( "<div class='failed'>Failed: Test Error</div>" );
    }

    @Test
    @FeatureDuration
    public void the_duration_of_steps_are_reported() {
        given().a_report_model_with_one_scenario()
            .and().step_$_has_a_duration_of_$_nano_seconds( 1, 123456789 );
        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_text( "<span class='duration'>(123.46 ms)</span>" );
    }

    @Test
    @FeatureDuration
    public void the_duration_of_scenarios_are_reported() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_a_duration_of_$_nano_seconds( 123456789 );
        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_text( "<span class='duration'>(123.46 ms)</span>" );
    }

    @Test
    @FeatureTableStepArguments
    public void the_static_HTML_report_generator_handles_data_table_arguments() throws IOException {
        given().a_report_model()
            .and().a_step_has_a_data_table_with_following_values(asList(
                asList("header1", "header2"),
                asList("value1", "value2"),
                asList("value3", "value4")));
        when().the_HTML_report_is_generated();
        then().the_HTML_report_contains_pattern( "<table class='data-table'>.*\n" +
                "<tr>.*<th>.*header1.*</th>.*<th>.*header2.*</th>.*</tr>.*\n" +
                "<tr>.*<td>.*value1.*</td>.*<td>.*value2.*</td>.*</tr>.*\n" +
                "<tr>.*<td>.*value3.*</td>.*<td>.*value4.*</td>.*</tr>.*\n" +
                "</table>" );
    }
}
