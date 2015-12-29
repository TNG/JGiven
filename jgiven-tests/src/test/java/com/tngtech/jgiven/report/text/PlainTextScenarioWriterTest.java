package com.tngtech.jgiven.report.text;

import static java.util.Arrays.asList;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.report.model.GivenReportModel;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.tags.FeatureDataTables;
import com.tngtech.jgiven.tags.FeatureTextReport;
import com.tngtech.jgiven.tags.Issue;

@FeatureTextReport
@RunWith( DataProviderRunner.class )
public class PlainTextScenarioWriterTest extends JGivenScenarioTest<GivenReportModel<?>, WhenPlainTextWriter, ThenPlainTextOutput> {

    @DataProvider
    public static Object[][] statusTexts() {
        return new Object[][] {
            { StepStatus.PASSED, "something happens" },
            { StepStatus.FAILED, "something happens (failed)" },
            { StepStatus.SKIPPED, "something happens (skipped)" },
            { StepStatus.PENDING, "something happens (pending)" },
        };
    }

    @Test
    @UseDataProvider( "statusTexts" )
    public void ignored_steps_marked_in_text_reports( StepStatus status, String expectedText ) throws UnsupportedEncodingException {
        given()
            .a_report_model_with_one_scenario()
            .and().step_$_is_named( 1, "something happens" )
            .and().step_$_has_status( 1, status );

        when().the_plain_text_report_is_generated();

        then().the_report_contains_text( expectedText );
    }

    @Test
    public void cases_are_generated_in_text_reports() throws UnsupportedEncodingException {
        given()
            .a_report_model_with_one_scenario()
            .and().the_scenario_has_$_default_cases( 2 )
            .and().case_$_has_a_when_step_$_with_argument( 1, "some step", "someArg" );

        when().the_plain_text_report_is_generated();
        then().the_report_contains_text( "Case 1:" )
            .and().the_report_contains_text( "Case 2:" )
            .and().the_report_contains_text( "When some step someArg" );

    }

    @Test
    @FeatureDataTables
    @Issue( "#10" )
    public void arguments_are_correctly_printed_in_text_reports_for_data_tables() throws UnsupportedEncodingException {
        given()
            .a_report_model_with_one_scenario()
            .and().the_scenario_has_parameters( "param1" )
            .and().the_scenario_has_$_default_cases( 2 )
            .and().case_$_has_arguments( 1, "arg10" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "some arg step", "arg10", "aArg" )
            .and().case_$_has_arguments( 2, "arg20" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "some arg step", "arg20", "aArg" )
            .and().all_cases_have_a_step_$_with_argument( "some step", "someArg" );

        when().the_plain_text_report_is_generated();

        then().the_report_contains_text( "some step someArg" )
            .and().the_report_contains_text( "some arg step <param1>" );
    }

    @Test
    @FeatureDataTables
    @Issue( "#34" )
    public void data_tables_are_generated_correctly_in_text_reports() throws UnsupportedEncodingException {
        given()
            .a_report_model_with_one_scenario()
            .and().the_scenario_has_$_default_cases( 3 )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "some arg step", "43", "aArg1" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "another arg step", "arg11", "aArg2" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "some arg step", "4", "aArg1" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "another arg step", "arg21", "aArg2" )
            .and().case_$_fails_with_error_message( 2, "Some Error" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 3, "some arg step", "1234567", "aArg1" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 3, "another arg step", "arg31", "aArg2" );

        when().the_plain_text_report_is_generated();

        then().the_report_contains_text( "<aArg1>" )
            .and().the_report_contains_text( "<aArg2>" )
            .and().the_report_contains_text( "\n" +
                    "   | # |   aArg1 | aArg2 | Status             |\n" +
                    "   +---+---------+-------+--------------------+\n" +
                    "   | 1 |      43 | arg11 | Success            |\n" +
                    "   | 2 |       4 | arg21 | Failed: Some Error |\n" +
                    "   | 3 | 1234567 | arg31 | Success            |\n" );
    }

    @Test
    @FeatureDataTables
    @Issue( "#104" )
    public void parameters_with_equal_values_but_different_formatting_result_in_different_placeholders()
            throws UnsupportedEncodingException {
        given()
            .a_report_model_with_one_scenario()
            .and().the_scenario_has_$_cases( 2 )
            .and().parameters( "aParam", "anotherParam" )
            .given().case_$_has_arguments( 1, "false", "false" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "some arg step", "false", "anArg" )
            .with().formatted_value( "off" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "another arg step", "false", "anotherArg" )
            .with().formatted_value( "is not" )
            .given().case_$_has_arguments( 2, "true", "true" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "some arg step", "true", "anArg" )
            .with().formatted_value( "on" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "another arg step", "true", "anotherArg" )
            .with().formatted_value( "is" );

        when().the_plain_text_report_is_generated();

        then().the_report_contains_text( "<anArg>" )
            .and().the_report_contains_text( "<anotherArg>" )
            .and().the_report_contains_text( "\n" +
                    "   | # | anArg | anotherArg | Status  |\n" +
                    "   +---+-------+------------+---------+\n" +
                    "   | 1 | off   | is not     | Success |\n" +
                    "   | 2 | on    | is         | Success |\n" );
    }

    @Test
    @FeatureDataTables
    public void data_tables_are_generated_for_empty_strings() throws UnsupportedEncodingException {
        given()
            .a_report_model_with_one_scenario()
            .and().the_scenario_has_$_default_cases( 2 )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "some arg step", "non empty string", "arg" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "some arg step", "", "arg" );

        when().the_plain_text_report_is_generated();

        then().the_report_contains_text( "<arg>" )
            .and().the_report_contains_text( "\n" +
                    "   | # | arg              | Status  |\n" +
                    "   +---+------------------+---------+\n" +
                    "   | 1 | non empty string | Success |\n" +
                    "   | 2 |                  | Success |\n" );
    }

    @Test
    @Issue( "#52" )
    @FeatureDataTables
    @DataProvider( {
        "VERTICAL, false",
        "HORIZONTAL, true",
        "NONE, false",
        "BOTH, true"
    } )
    public void table_annotations_at_parameters_lead_to_data_tables_in_the_report( Table.HeaderType headerType, boolean hasHeaderLine )
            throws UnsupportedEncodingException {
        given().a_report_model_with_one_scenario()
            .and().a_step_has_a_data_table_with_following_values( asList(
                asList( "foo", "bar" ),
                asList( "1", "a" ),
                asList( "2", "b" ) ) )
            .with().header_type_set_to( headerType );
        when().the_plain_text_report_is_generated();
        then().the_report_contains_text( "\n" +
                "     | foo | bar |\n" +
                ( hasHeaderLine ? "     +-----+-----+\n" : "" ) +
                "     |   1 | a   |\n" +
                "     |   2 | b   |\n" );
    }

    @Test
    @FeatureDataTables
    public void a_description_column_is_generated_if_cases_have_a_description() throws UnsupportedEncodingException {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_$_default_cases( 2 )
            .and().case_$_has_description( 1, "some test description" )
            .and().case_$_has_description( 2, "another case" );

        when().the_plain_text_report_is_generated();
        then().the_report_contains_text( "\n" +
                "   | # | Description           | Status  |\n" +
                "   +---+-----------------------+---------+\n" +
                "   | 1 | some test description | Success |\n" +
                "   | 2 | another case          | Success |\n" );

    }

    @Test
    @FeatureDataTables
    @Issue( "#152" )
    public void new_lines_in_data_tables_do_not_break_the_table_layout() throws UnsupportedEncodingException {
        given()
            .a_report_model_with_one_scenario()
            .and().the_scenario_has_$_default_cases( 2 )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "some arg step", "1\n2", "aArg1" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "some arg step", "4", "aArg1" );

        when().the_plain_text_report_is_generated();

        then().the_report_contains_text( "<aArg1>" )
            .and().the_report_contains_text( "\n" +
                    "   | # | aArg1 | Status  |\n" +
                    "   +---+-------+---------+\n" +
                    "   | 1 |     1 | Success |\n" +
                    "   |   |     2 |         |\n" +
                    "   | 2 |     4 | Success |\n" );
    }
}
