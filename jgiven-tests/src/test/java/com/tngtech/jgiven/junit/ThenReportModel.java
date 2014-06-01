package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.StepModel;

public class ThenReportModel<SELF extends ThenReportModel<?>> extends Stage<SELF> {

    @ProvidedScenarioState
    ReportModel reportModel;

    public SELF step_$_is_reported_as_skipped( int i ) {
        assertThat( getStep( i ).isSkipped() ).isTrue();
        return self();
    }

    public SELF step_$_is_reported_as_failed( int i ) {
        assertThat( getStep( i ).isFailed() ).isTrue();
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

}
