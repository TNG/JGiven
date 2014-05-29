package com.tngtech.jgiven.report.model;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.List;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.impl.intercept.InvocationMode;
import com.tngtech.jgiven.report.impl.CaseArgumentAnalyser;

public class GivenReportModel<SELF extends GivenReportModel<?>> extends Stage<SELF> {

    @ProvidedScenarioState
    protected ReportModel reportModel;

    public SELF a_report_model_with_one_scenario() {
        return a_report_model();
    }

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

        addCase( scenarioModel );

        reportModel.scenarios.add( scenarioModel );
    }

    private void addCase( ScenarioModel scenarioModel ) {
        ScenarioCaseModel scenarioCaseModel = new ScenarioCaseModel();
        scenarioModel.addCase( scenarioCaseModel );
        int i = 0;
        for( String param : scenarioModel.parameterNames ) {
            scenarioCaseModel.addArguments( "arg" + scenarioCaseModel.caseNr + i++ );
        }
        scenarioCaseModel.addStep( "something_happens", Arrays.asList( Word.introWord( "given" ), new Word( "something" ) ),
            InvocationMode.NORMAL );
        if( !scenarioCaseModel.arguments.isEmpty() ) {
            scenarioCaseModel.addStep( "something_happens", asList( Word.introWord( "when" ),
                Word.argWord( scenarioCaseModel.arguments.get( 0 ) ) ), InvocationMode.NORMAL );
        }
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

    public SELF the_scenario_has_parameters( String... params ) {
        reportModel.getLastScenarioModel().addParameterNames( params );
        return self();
    }

    public SELF the_scenario_has_$_cases( int ncases ) {
        reportModel.getLastScenarioModel().clearCases();
        for( int i = 0; i < ncases; i++ ) {
            addCase( reportModel.getLastScenarioModel() );
        }
        return self();
    }

    public SELF case_$_has_arguments( int ncase, String... args ) {
        List<String> arguments = getCase( ncase ).arguments;
        arguments.clear();
        arguments.addAll( Arrays.asList( args ) );
        return self();
    }

    private ScenarioCaseModel getCase( int ncase ) {
        return reportModel.getLastScenarioModel().getScenarioCases().get( ncase - 1 );
    }

    public SELF step_$_is_named( int i, String name ) {
        getCase( 1 ).getStep( i - 1 ).words.get( 1 ).setValue( name );
        return self();
    }

    public SELF step_$_has_status( int i, StepStatus status ) {
        getCase( 1 ).getStep( i - 1 ).setStatus( status );
        return self();
    }

    public SELF case_$_has_a_when_step_$_with_argument( int ncase, String name, String arg ) {
        getCase( ncase ).addStep( name, Arrays.asList( Word.introWord( "when" ), new Word( name ), Word.argWord( arg ) ),
            InvocationMode.NORMAL );
        return self();
    }

    public SELF the_first_scenario_has_tag( String name ) {
        return scenario_$_has_tag_$_with_value_$( 1, name, null );
    }

    public SELF scenario_$_has_tag_$_with_value_$( int i, String name, String value ) {
        reportModel.scenarios.get( i - 1 ).tags.add( new Tag( name, value ) );
        return self();
    }

    @AfterStage
    public void analyzeReport() {
        new CaseArgumentAnalyser().analyze( reportModel );
    }
}
