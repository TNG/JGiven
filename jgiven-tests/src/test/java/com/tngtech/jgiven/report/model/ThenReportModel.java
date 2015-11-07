package com.tngtech.jgiven.report.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ExtendedDescription;

public class ThenReportModel<SELF extends ThenReportModel<?>> extends Stage<SELF> {

    @ExpectedScenarioState
    protected ReportModel reportModel;

    public SELF step_$_is_reported_as_skipped( int i ) {
        assertThat( getStep( i ).isSkipped() ).isTrue();
        return self();
    }

    public SELF step_$_is_reported_as_failed( int i ) {
        assertThat( getStep( i ).isFailed() ).isTrue();
        return self();
    }

    public SELF step_$_is_reported_as_passed( int i ) {
        assertThat( getStep( i ).getStatus() ).isEqualTo( StepStatus.PASSED );
        return self();
    }

    private StepModel getStep( int i ) {
        return getFirstCase().getStep( i - 1 );
    }

    private ScenarioCaseModel getFirstCase() {
        return reportModel.getLastScenarioModel().getCase( 0 );
    }

    public SELF the_case_is_marked_as_failed() {
        assertThat( getFirstCase().isSuccess() ).isFalse();
        return self();
    }

    public void an_error_message_is_stored_in_the_report() {
        assertThat( getFirstCase().getErrorMessage() ).isNotNull();
    }

    public void the_report_model_contains_a_tag_named( String tagName ) {
        List<String> tags = reportModel.getLastScenarioModel().getTagIds();
        assertThat( tags ).isNotEmpty();
        assertThat( tags ).contains( tagName + "-testValue" );
    }

    public void the_description_of_the_report_model_is( String description ) {
        assertThat( reportModel.getDescription() ).isEqualTo( description );
    }

    @ExtendedDescription( "With version 4.12 JUnit changed its behavior regarding test classes where all tests are @Ignored. " +
            "Instead of executing class-level test rules, no rules are executed at all. " +
            "In that case no report model will be generated at all by JGiven. " +
            "For earlier JUnit versions JGiven will generate empty report models" )
    public SELF the_report_model_is_either_null_or_empty() {
        assertThat( reportModel == null || reportModel.getScenarios().isEmpty() )
            .as( "Report model is either null or empty" ).isTrue();
        return self();
    }

    public SELF the_report_model_contains_$_scenarios( int nScenarios ) {
        assertThat( reportModel.getScenarios() ).hasSize( nScenarios );
        return self();
    }

    public SELF word_$_of_step_$_of_case_$_is_marked_as_diff( int wordNr, int stepNr, int caseNr ) {
        return isDifferent( wordNr, stepNr, caseNr, true );
    }

    public SELF word_$_of_step_$_of_case_$_is_not_marked_as_diff( int wordNr, int stepNr, int caseNr ) {
        return isDifferent( wordNr, stepNr, caseNr, false );
    }

    private SELF isDifferent( int wordNr, int stepNr, int caseNr, boolean expected ) {
        Word word = getWord( caseNr, stepNr, wordNr );
        assertThat( word.isDifferent() ).isEqualTo( expected );
        return self();
    }

    private Word getWord( int caseNr, int stepNr, int wordNr ) {
        return reportModel.getLastScenarioModel().getCase( caseNr - 1 ).getStep( stepNr - 1 ).getWord( wordNr - 1 );
    }

    public SELF case_$_has_derived_arguments( int caseNr, String... arguments ) {
        assertThat( reportModel.getLastScenarioModel().getCase( caseNr - 1 ).getDerivedArguments() ).containsExactly( arguments );
        return self();
    }

    public SELF the_scenario_has_no_derived_parameters() {
        assertThat( reportModel.getLastScenarioModel().getDerivedParameters() ).isEmpty();
        return self();
    }

    public SELF the_scenario_has_derived_parameters( String... parameters ) {
        assertThat( reportModel.getLastScenarioModel().getDerivedParameters() ).containsExactly( parameters );
        return self();
    }

    public SELF the_report_model_contains_one_scenario_with_$_cases( int nCases ) {
        assertThat( reportModel.getLastScenarioModel().getScenarioCases() ).hasSize( nCases );
        return self();
    }

    public SELF case_$_has_status( int i, ExecutionStatus status ) {
        assertThat( reportModel.getLastScenarioModel().getScenarioCases().get( i - 1 ).getExecutionStatus() ).isEqualTo( status );
        return self();
    }
}
