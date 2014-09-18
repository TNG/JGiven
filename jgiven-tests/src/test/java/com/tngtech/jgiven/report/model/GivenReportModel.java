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
        reportModel.setClassName( "Test Class" );

        createScenarioModel( "something should happen", "something_should_happen" );

        return self();
    }

    private void createScenarioModel( String description, String testMethodName ) {
        ScenarioModel scenarioModel = new ScenarioModel();
        scenarioModel.className = reportModel.getClassName();
        scenarioModel.description = description;
        scenarioModel.testMethodName = testMethodName;

        addCase( scenarioModel );

        reportModel.getScenarios().add( scenarioModel );
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
        i = 0;
        for( String arg : scenarioCaseModel.arguments ) {
            scenarioCaseModel.addStep( "something_happens", asList( Word.introWord( "when" ),
                Word.argWord( "stepArg" + i++, arg ) ), InvocationMode.NORMAL );
        }
    }

    public SELF a_report_model_with_name( String name ) {
        a_report_model();
        reportModel.setClassName( name );
        for( ScenarioModel model : reportModel.getScenarios() ) {
            model.className = name;
        }
        return self();
    }

    public void the_report_has_$_scenarios( int n ) {
        reportModel.getScenarios().clear();
        for( int i = 0; i < n; i++ ) {
            createScenarioModel( "something should happen " + i, "something_should_happen_" + i );
        }
    }

    public ReportModel getReportModel() {
        return reportModel;
    }

    public SELF the_scenario_has_parameters( String... params ) {
        reportModel.getLastScenarioModel().addParameterNames( params );
        return self();
    }

    public SELF the_scenario_has_a_duration_of_$_nano_seconds( long durationInNanos ) {
        reportModel.getLastScenarioModel().setDurationInNanos( durationInNanos );
        return self();
    }

    public SELF the_scenario_has_$_cases( int ncases ) {
        reportModel.getLastScenarioModel().clearCases();
        for( int i = 0; i < ncases; i++ ) {
            addCase( reportModel.getLastScenarioModel() );
        }
        return self();
    }

    public SELF case_$_fails_with_error_message( int ncase, String errorMessage ) {
        getCase( ncase ).errorMessage = errorMessage;
        getCase( ncase ).success = false;
        return self();
    }

    public SELF case_$_has_arguments( int ncase, String... args ) {
        List<String> arguments = getCase( ncase ).arguments;
        arguments.clear();
        arguments.addAll( Arrays.asList( args ) );
        return self();
    }

    public SELF all_cases_have_a_step_$_with_argument( String name, String arg ) {
        int i = 1;
        for( ScenarioCaseModel caseModel : reportModel.getLastScenarioModel().getScenarioCases() ) {
            case_$_has_a_step_$_with_argument( i++, name, arg );
        }
        return self();
    }

    public SELF case_$_has_a_step_$_with_argument( int i, String name, String arg ) {
        return case_$_has_a_when_step_$_with_argument( i, name, arg );
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

    public SELF step_$_has_a_duration_of_$_nano_seconds( int i, long durationInNanos ) {
        getCase( 1 ).getStep( i - 1 ).setDurationInNanos( durationInNanos );
        return self();
    }

    public SELF case_$_has_a_when_step_$_with_argument( int ncase, String name, String arg ) {
        return case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( ncase, name, arg, "argName" );
    }

    public SELF case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( int ncase, String name, String arg, String argName ) {
        getCase( ncase ).addStep( name, Arrays.asList( Word.introWord( "when" ), new Word( name ), Word.argWord( argName, arg ) ),
            InvocationMode.NORMAL );
        return self();
    }

    public SELF the_first_scenario_has_tag( String name ) {
        return scenario_$_has_tag_$_with_value_$( 1, name, null );
    }

    public SELF scenario_$_has_tag_$_with_value_$( int i, String name, String value ) {
        reportModel.getScenarios().get( i - 1 ).tags.add( new Tag( name, value ).setPrependType( true ) );
        return self();
    }

    @AfterStage
    public void analyzeReport() {
        new CaseArgumentAnalyser().analyze( reportModel );
    }
}
