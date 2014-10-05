package com.tngtech.jgiven.report.html;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.report.WhenReportGenerator;
import com.tngtech.jgiven.report.json.GivenJsonReports;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.tags.FeatureHtmlReport;
import com.tngtech.jgiven.tags.FeatureTags;
import com.tngtech.jgiven.tags.Issue;

@RunWith( DataProviderRunner.class )
@FeatureHtmlReport
public class StaticHtmlReportGeneratorTest extends
        JGivenScenarioTest<GivenJsonReports<?>, WhenReportGenerator<?>, ThenStaticHtmlReportGenerator<?>> {

    @Test
    @DataProvider( { "0", "1", "3" } )
    public void the_static_HTML_reporter_generates_one_file_for_each_test_class( int n ) throws IOException {
        given().$_report_models( n )
            .and().the_reports_exist_as_JSON_files();

        when().the_static_HTML_reporter_is_executed();

        then().an_index_file_exists()
            .and().an_HTML_file_exists_for_each_test_class();
    }

    @Test
    @FeatureTags
    public void the_static_HTML_reporter_generates_one_file_for_each_tag() throws IOException {
        given().a_report_model()
            .and().the_first_scenario_has_tag( "TestTag" )
            .and().the_report_exist_as_JSON_file();

        when().the_static_HTML_reporter_is_executed();

        then().a_file_with_name_$_exists( "TestTag.html" );
    }

    @Test
    @FeatureTags
    public void the_static_HTML_reporter_generates_one_file_for_each_tag_value() throws IOException {
        given().a_report_model()
            .and().the_report_has_$_scenarios( 2 )
            .and().scenario_$_has_tag_$_with_value_$( 1, "TestTag", "123" )
            .and().scenario_$_has_tag_$_with_value_$( 2, "TestTag", "456" )
            .and().the_report_exist_as_JSON_file();

        when().the_static_HTML_reporter_is_executed();

        then().a_file_with_name_$_exists( "TestTag-123.html" )
            .and().a_file_with_name_$_exists( "TestTag-456.html" );
    }

    @Test
    @Issue( "#29" )
    public void the_static_HTML_report_generates_one_file_for_all_failed_scenarios() throws IOException {
        given().a_report_model()
            .and().the_report_has_$_scenarios( 1 )
            .and().case_$_of_scenario_$_has_failed( 1, 1 )
            .and().the_report_exist_as_JSON_file();
        when().the_static_HTML_reporter_is_executed();
        then().a_file_with_name_$_exists( "failed.html" );
    }

    @Test
    @Issue( "#33" )
    public void the_failed_file_generated_by_the_static_HTML_report_generator_contains_scenarios_where_some_cases_are_failed()
            throws IOException {
        given().a_report_model()
            .and().the_report_has_$_scenarios( 1 )
            .and().the_scenario_has_$_default_cases( 2 )
            .and().case_$_of_scenario_$_has_failed( 1, 1 )
            .and().step_$_of_case_$_has_status( 1, 1, StepStatus.FAILED )
            .and().the_report_exist_as_JSON_file();
        when().the_static_HTML_reporter_is_executed();
        then().a_file_with_name_$_exists( "failed.html" )
            .and().file_$_contains_scenario_$( "failed.html", 1 );
    }

    @Test
    @Issue( "#33" )
    public void the_failed_file_generated_by_the_static_HTML_report_generator_contains_failed_scenarios_where_all_steps_are_successful()
            throws IOException {
        given().a_report_model()
            .and().the_report_has_$_scenarios( 1 )
            .and().case_$_of_scenario_$_has_failed( 1, 1 )
            .and().the_report_exist_as_JSON_file();
        when().the_static_HTML_reporter_is_executed();
        then().a_file_with_name_$_exists( "failed.html" )
            .and().file_$_contains_scenario_$( "failed.html", 1 );
    }

}
