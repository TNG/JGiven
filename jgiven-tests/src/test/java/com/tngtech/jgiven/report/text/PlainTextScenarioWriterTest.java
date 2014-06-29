package com.tngtech.jgiven.report.text;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.JGivenTestConfiguration;
import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.report.model.GivenReportModel;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.tags.FeatureDataTables;
import com.tngtech.jgiven.tags.FeatureTextReport;
import com.tngtech.jgiven.tags.Issue;

@FeatureTextReport
@RunWith( DataProviderRunner.class )
@JGivenConfiguration( JGivenTestConfiguration.class )
public class PlainTextScenarioWriterTest extends ScenarioTest<GivenReportModel<?>, WhenPlainTextWriter, ThenPlainTextOutput> {

    @DataProvider
    public static Object[][] statusTexts() {
        return new Object[][] {
            { StepStatus.PASSED, "something happens" },
            { StepStatus.FAILED, "something happens (failed)" },
            { StepStatus.SKIPPED, "something happens (skipped)" },
            { StepStatus.NOT_IMPLEMENTED_YET, "something happens (not implemented yet)" },
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
            .and().the_scenario_has_$_cases( 2 )
            .and().case_$_has_a_when_step_$_with_argument( 1, "some step", "someArg" );

        when().the_plain_text_report_is_generated();
        then().the_report_contains_text( "Case 1:" )
            .and().the_report_contains_text( "Case 2:" )
            .and().the_report_contains_text( "When some step 'someArg'" );

    }

    @Test
    @FeatureDataTables
    @Issue( "#10" )
    public void arguments_are_correctly_printed_in_text_reports_for_data_tables() throws UnsupportedEncodingException {
        given()
            .a_report_model_with_one_scenario()
            .and().the_scenario_has_parameters( "param1" )
            .and().the_scenario_has_$_cases( 2 )
            .and().case_$_has_arguments( 1, "arg10" )
            .and().case_$_has_a_step_$_with_argument( 1, "some arg step", "arg10" )
            .and().case_$_has_arguments( 2, "arg20" )
            .and().case_$_has_a_step_$_with_argument( 2, "some arg step", "arg20" )
            .and().all_cases_have_a_step_$_with_argument( "some step", "someArg" );

        when().the_plain_text_report_is_generated();

        then().the_report_contains_text( "some step 'someArg'" )
            .and().the_report_contains_text( "some arg step <param1>" );
    }

    @Test
    @FeatureDataTables
    public void data_tables_are_generated_in_text_reports() throws UnsupportedEncodingException {
        given()
            .a_report_model_with_one_scenario()
            .and().the_scenario_has_parameters( "param1", "param2" )
            .and().the_scenario_has_$_cases( 3 )
            .and().case_$_has_arguments( 1, "arg10", "arg11" )
            .and().case_$_has_arguments( 2, "arg20", "arg21" )
            .and().case_$_has_arguments( 3, "arg30", "arg31" );

        when().the_plain_text_report_is_generated();

        then().the_report_contains_text( "<param1>" )
            .and().the_report_contains_text( "<param2>" )
            .and().the_report_contains_text( "\n" +
                    "    | param1 | param2 |\n" +
                    "    +--------+--------+\n" +
                    "    |  arg10 |  arg11 |\n" +
                    "    |  arg20 |  arg21 |\n" +
                    "    |  arg30 |  arg31 |\n" );
    }
}
