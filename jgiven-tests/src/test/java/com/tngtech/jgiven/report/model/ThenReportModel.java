package com.tngtech.jgiven.report.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class ThenReportModel<SELF extends ThenReportModel<?>> extends Stage<SELF> {

    @ProvidedScenarioState
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
        assertThat( getFirstCase().success ).isFalse();
        return self();
    }

    public void an_error_message_is_stored_in_the_report() {
        assertThat( getFirstCase().errorMessage ).isNotNull();
    }

    public void the_report_model_contains_a_tag_named( String tagName ) {
        Set<Tag> tags = reportModel.getLastScenarioModel().tags;
        assertThat( tags ).isNotEmpty();
        assertThat( tags ).extracting( "name" ).contains( tagName );
    }

    public void the_description_of_the_report_model_is( String description ) {
        assertThat( reportModel.getDescription() ).isEqualTo( description );
    }

    public void the_report_model_contains_$_scenarios( int nScenarios ) {
        assertThat( reportModel.getScenarios() ).hasSize( nScenarios );
    }

}
