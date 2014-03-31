package com.tngtech.jgiven.report.model;

import java.util.Arrays;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class GivenReportModel<SELF extends GivenReportModel<?>> extends Stage<SELF> {

    @ProvidedScenarioState
    protected ReportModel reportModel;

    public SELF a_report_model() {
        reportModel = new ReportModel();
        reportModel.className = "Test Class";

        createScenarioModel( "something_should_happen" );

        return self();
    }

    private void createScenarioModel( String testMethodName ) {
        ScenarioModel scenarioModel = new ScenarioModel();
        scenarioModel.className = reportModel.className;
        scenarioModel.description = "something should happen";
        scenarioModel.testMethodName = testMethodName;

        ScenarioCaseModel scenarioCaseModel = new ScenarioCaseModel();
        scenarioCaseModel.addStep( "something_happens", Arrays.asList( new Word( "something" ), new Word( "happens" ) ), false );

        scenarioModel.scenarioCases.add( scenarioCaseModel );

        reportModel.scenarios.add( scenarioModel );
    }

    public SELF a_report_model_with_name( String name ) {
        a_report_model();
        reportModel.className = name;
        for( ScenarioModel model : reportModel.scenarios ) {
            model.className = name;
        }
        return self();
    }

    public void the_report_has_$_scenarios( int n ) {
        reportModel.scenarios.clear();
        for( int i = 0; i < n; i++ ) {
            createScenarioModel( "something_should_happen_" + i );
        }
    }

    public ReportModel getReportModel() {
        return reportModel;
    }

}
