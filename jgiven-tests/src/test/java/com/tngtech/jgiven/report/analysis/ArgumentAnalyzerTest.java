package com.tngtech.jgiven.report.analysis;

import org.junit.Test;

import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.report.model.GivenReportModel;
import com.tngtech.jgiven.report.model.ThenReportModel;
import com.tngtech.jgiven.tags.Issue;

public class ArgumentAnalyzerTest extends JGivenScenarioTest<GivenReportModel<?>, WhenAnalyzer<?>, ThenReportModel<?>> {

    @Test
    @Issue( "#32" )
    public void multiple_parameter_usages_lead_to_one_parameter() {
        given().an_unanalyzed_report_model_with_one_scenario()
            .with().parameters( "param1" )
            .and().the_scenario_has_$_cases( 2 )
            .and().case_$_has_arguments( 1, "foo" )
            .and().case_$_has_a_step_$_with_argument( 1, "some step", "foo" )
            .and().case_$_has_a_step_$_with_argument( 1, "another step", "foo" )
            .and().case_$_has_arguments( 2, "bar" )
            .and().case_$_has_a_step_$_with_argument( 2, "some step", "bar" )
            .and().case_$_has_a_step_$_with_argument( 2, "another step", "bar" );
        when().the_argument_analyzer_is_executed();
        then().the_scenario_has_derived_parameters( "param1" )
            .and().case_$_has_derived_arguments( 1, "foo" )
            .and().case_$_has_derived_arguments( 2, "bar" );
    }

    @Test
    @Issue( "#163" )
    public void multiple_formatted_arguments_lead_to_one_parameter() {
        given().an_unanalyzed_report_model_with_one_scenario()
            .with().parameters( "param1" )
            .and().the_scenario_has_$_cases( 2 )
            .and().case_$_has_arguments( 1, "foo" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "some step", "'foo'", "arg1" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 1, "another step", "'foo'", "arg2" )
            .and().case_$_has_arguments( 2, "bar" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "some step", "'bar'", "arg1" )
            .and().case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( 2, "another step", "'bar'", "arg2" );
        when().the_argument_analyzer_is_executed();
        then().the_scenario_has_derived_parameters( "arg1" )
            .and().case_$_has_derived_arguments( 1, "'foo'" )
            .and().case_$_has_derived_arguments( 2, "'bar'" );
    }

    @Test
    @Issue( "#163" )
    public void different_structure_prevent_data_table() {
        given().an_unanalyzed_report_model_with_one_scenario()
            .with().parameters( "param1" )
            .and().the_scenario_has_$_cases( 2 )
            .and().case_$_has_arguments( 1, "foo" )
            .and().case_$_has_a_step_$_with_argument( 1, "some step", "foo" )
            .and().case_$_has_a_step_$_with_argument( 1, "another step", "foo" )
            .and().case_$_has_arguments( 2, "bar" )
            .and().case_$_has_a_step_$_with_argument( 2, "some step different to the case before", "bar" )
            .and().case_$_has_a_step_$_with_argument( 2, "another step", "bar" );
        when().the_argument_analyzer_is_executed();
        then().the_scenario_has_no_derived_parameters();
    }

}
