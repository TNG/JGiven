package com.tngtech.jgiven.report;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.report.json.GivenJsonReports;
import com.tngtech.jgiven.report.model.GivenReportModel;
import com.tngtech.jgiven.report.model.ThenCompleteReportModel;

@RunWith( DataProviderRunner.class )
public class ReportGeneratorTest extends JGivenScenarioTest<GivenReportModel<?>, WhenReportGenerator<?>, ThenCompleteReportModel<?>> {

    @ScenarioStage
    GivenJsonReports<?> jsonReports;

    @Test
    @DataProvider( {
        "true, 0",
        "false, 1" } )
    public void the_exclude_empty_scenarios_option_is_evaluated( boolean excludeEmptyScenarios, int expectedScenarios ) throws Exception {

        given().a_report_model()
            .and().the_report_has_$_scenarios( 1 )
            .and().the_scenario_has_$_cases( 1 )
            .and().case_$_has_no_steps( 1 );
        jsonReports
            .and().the_report_exist_as_JSON_file();

        when().the_exclude_empty_scenarios_option_is_set_to( excludeEmptyScenarios )
            .and().reading_the_report_model();

        then().the_report_model_contains_$_scenarios( expectedScenarios );
    }

    @Test
    @DataProvider( {
        "true, 1",
        "false, 2" } )
    public void empty_report_files_are_excluded_when_the_exclude_empty_scenarios_option_is_set( boolean excludeEmptyScenarios,
            int expectedReports ) throws Exception {

        jsonReports.given().a_report_model_with_name( "non empty report model" )
            .and().the_report_has_$_scenarios( 2 )
            .given().a_report_model_with_name( "empty report model" )
            .and().the_report_has_$_scenarios( 1 )
            .and().scenario_$_has_no_steps( 1 )
            .and().the_report_exist_as_JSON_file();

        when().the_exclude_empty_scenarios_option_is_set_to( excludeEmptyScenarios )
            .and().reading_the_report_model();

        then().the_report_model_contains_$_reports( expectedReports );
    }
}
