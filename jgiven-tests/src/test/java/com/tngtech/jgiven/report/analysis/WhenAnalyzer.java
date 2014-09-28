package com.tngtech.jgiven.report.analysis;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.report.model.ReportModel;

public class WhenAnalyzer<SELF extends WhenAnalyzer<?>> extends Stage<SELF> {

    @ScenarioState
    protected ReportModel reportModel;

    public SELF the_argument_analyzer_is_executed() {
        new CaseArgumentAnalyser().analyze( reportModel.getLastScenarioModel() );
        return self();
    }

    public SELF the_difference_analyzer_is_executed() {
        new CaseDifferenceAnalyzer().analyze( reportModel.getLastScenarioModel() );
        return self();
    }
}
