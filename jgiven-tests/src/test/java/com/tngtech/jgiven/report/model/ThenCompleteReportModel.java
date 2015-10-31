package com.tngtech.jgiven.report.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;

public class ThenCompleteReportModel<SELF extends ThenCompleteReportModel<SELF>> extends Stage<SELF> {

    @ExpectedScenarioState
    protected CompleteReportModel completeReportModel;

    public SELF the_report_model_contains_$_scenarios( int nScenarios ) {
        assertThat( completeReportModel.getAllScenarios() ).hasSize( nScenarios );
        return self();
    }

    public SELF the_report_model_contains_$_reports( int nReports ) {
        assertThat( completeReportModel.getAllReportModels() ).hasSize( nReports );
        return self();
    }
}
