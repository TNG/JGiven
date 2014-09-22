package com.tngtech.jgiven.report.analysis;

import org.junit.Test;

import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.report.model.GivenReportModel;
import com.tngtech.jgiven.report.model.ThenReportModel;
import com.tngtech.jgiven.tags.FeatureCaseDiffs;

public class DifferenceAnalyzerTest extends JGivenScenarioTest<GivenReportModel<?>, WhenAnalyzer, ThenReportModel<?>> {

    @Test
    @FeatureCaseDiffs
    public void the_difference_analyzer_should_find_differences_in_step_arguments() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_$_cases( 2 )
            .and().case_$_has_a_step_$_with_argument( 1, "some step", "foo" )
            .and().case_$_has_a_step_$_with_argument( 2, "some step", "bar" );
        when().the_difference_analyzer_is_executed();
        then().word_$_of_step_$_of_case_$_is_not_marked_as_diff( 2, 1, 1 )
            .and().word_$_of_step_$_of_case_$_is_marked_as_diff( 3, 1, 1 )
            .and().word_$_of_step_$_of_case_$_is_not_marked_as_diff( 2, 1, 2 )
            .and().word_$_of_step_$_of_case_$_is_marked_as_diff( 3, 1, 2 );
    }

    @Test
    @FeatureCaseDiffs
    public void the_difference_analyzer_should_find_additional_steps_at_the_end() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_$_cases( 2 )
            .and().case_$_has_step_$( 1, "some step" )
            .and().case_$_has_step_$( 1, "another step" )
            .and().case_$_has_step_$( 2, "some step" );
        when().the_difference_analyzer_is_executed();
        then().word_$_of_step_$_of_case_$_is_not_marked_as_diff( 2, 1, 1 )
            .and().word_$_of_step_$_of_case_$_is_marked_as_diff( 2, 2, 1 )
            .and().word_$_of_step_$_of_case_$_is_not_marked_as_diff( 2, 1, 2 );
    }

    @Test
    @FeatureCaseDiffs
    public void the_difference_analyzer_should_find_additional_steps_at_the_beginning() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_$_cases( 2 )
            .and().case_$_has_step_$( 1, "extra step at the beginning" )
            .and().case_$_has_step_$( 1, "some step" )
            .and().case_$_has_step_$( 2, "some step" );
        when().the_difference_analyzer_is_executed();
        then().word_$_of_step_$_of_case_$_is_marked_as_diff( 2, 1, 1 )
            .and().word_$_of_step_$_of_case_$_is_not_marked_as_diff( 2, 2, 1 )
            .and().word_$_of_step_$_of_case_$_is_not_marked_as_diff( 2, 1, 2 );
    }

    @Test
    @FeatureCaseDiffs
    public void the_difference_analyzer_should_find_additional_steps_in_the_middle() {
        given().a_report_model_with_one_scenario()
            .and().the_scenario_has_$_cases( 2 )
            .and().case_$_has_step_$( 1, "some step" )
            .and().case_$_has_step_$( 1, "extra step in the middle" )
            .and().case_$_has_step_$( 1, "another step" )
            .and().case_$_has_step_$( 2, "some step" )
            .and().case_$_has_step_$( 2, "another step" );
        when().the_difference_analyzer_is_executed();
        then().word_$_of_step_$_of_case_$_is_not_marked_as_diff( 2, 1, 1 )
            .and().word_$_of_step_$_of_case_$_is_marked_as_diff( 2, 2, 1 )
            .and().word_$_of_step_$_of_case_$_is_not_marked_as_diff( 2, 3, 1 )
            .and().word_$_of_step_$_of_case_$_is_not_marked_as_diff( 2, 1, 2 )
            .and().word_$_of_step_$_of_case_$_is_not_marked_as_diff( 2, 2, 2 );
    }
}
