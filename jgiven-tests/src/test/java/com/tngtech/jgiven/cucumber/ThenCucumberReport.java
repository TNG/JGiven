package com.tngtech.jgiven.cucumber;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.cucumber.json.CucumberJsonReport;
import com.tngtech.jgiven.report.model.ReportModel;

public class ThenCucumberReport extends Stage<ThenCucumberReport> {

    @ProvidedScenarioState
    CucumberJsonReport cucumberReport;

    @ProvidedScenarioState
    private List<ReportModel> reportModels;

    public ThenCucumberReport the_result_is_correct() {
        return self();
    }

    public void the_JGiven_report_matches_the_Cucumber_report() {
        assertThat( cucumberReport.features.size() ).isEqualTo( reportModels.size() );
    }
}
