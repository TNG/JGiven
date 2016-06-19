package com.tngtech.jgiven.report.model;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;
import com.tngtech.jgiven.report.analysis.CaseArgumentAnalyser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class GivenReportModel<SELF extends GivenReportModel<?>> extends Stage<SELF> {

    @ProvidedScenarioState
    protected ReportModel reportModel;

    private boolean analyze = true;
    private Tag latestTag;
    private Word latestWord;

    private Word lastArgWord;

    @ExtendedDescription( "A report model where the analysers have not been executed on" )
    public SELF an_unanalyzed_report_model_with_one_scenario() {
        analyze = false;
        return a_report_model_with_one_scenario();
    }

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
        scenarioModel.setClassName( reportModel.getClassName() );
        scenarioModel.setDescription( description );
        scenarioModel.setTestMethodName( testMethodName );

        addDefaultCase( scenarioModel );

        reportModel.getScenarios().add( scenarioModel );
    }

    private void addDefaultCase( ScenarioModel scenarioModel ) {
        ScenarioCaseModel scenarioCaseModel = new ScenarioCaseModel();
        scenarioModel.addCase( scenarioCaseModel );
        int i = 0;
        for( String param : scenarioModel.getExplicitParameters() ) {
            scenarioCaseModel.addExplicitArguments( "arg" + scenarioCaseModel.getCaseNr() + i++ );
        }

        scenarioCaseModel
                .addStep( new StepModel( "something_happens", Arrays.asList( Word.introWord( "given" ), new Word( "something" ) ) ) );
        i = 0;
        for( String arg : scenarioCaseModel.getExplicitArguments() ) {
            String argumentName = "stepArg" + i++;
            scenarioCaseModel.addStep( new StepModel( "something_happens", asList( Word.introWord( "when" ),
                    Word.argWord( argumentName, arg, (String) null ) ) ) );
        }
    }

    public SELF a_report_model_with_name( String name ) {
        a_report_model();
        reportModel.setClassName( name );
        for( ScenarioModel model : reportModel.getScenarios() ) {
            model.setClassName( name );
        }
        return self();
    }

    public SELF the_report_has_$_scenarios( int n ) {
        reportModel.getScenarios().clear();
        for( int i = 0; i < n; i++ ) {
            createScenarioModel( "something should happen " + i, "something_should_happen_" + i );
        }
        return self();
    }

    public ReportModel getReportModel() {
        return reportModel;
    }

    public SELF parameters( String... params ) {
        return the_scenario_has_parameters( params );
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
        ScenarioModel scenarioModel = reportModel.getLastScenarioModel();
        scenarioModel.clearCases();
        for( int i = 0; i < ncases; i++ ) {
            scenarioModel.addCase( new ScenarioCaseModel() );
        }
        return self();
    }

    public SELF the_scenario_has_$_default_cases( int ncases ) {
        reportModel.getLastScenarioModel().clearCases();
        for( int i = 0; i < ncases; i++ ) {
            addDefaultCase( reportModel.getLastScenarioModel() );
        }
        return self();
    }

    public SELF case_$_of_scenario_$_has_failed( int caseNr, int scenarioNr ) {
        getCase( scenarioNr, caseNr ).setSuccess( false );
        return self();
    }

    public SELF case_$_fails_with_error_message( int ncase, String errorMessage ) {
        getCase( ncase ).setErrorMessage( errorMessage );
        getCase( ncase ).setSuccess( false );
        return self();
    }

    public SELF case_$_has_arguments( int ncase, String... args ) {
        getCase( ncase ).setExplicitArguments( Arrays.asList( args ) );
        return self();
    }

    public SELF case_$_has_description( int ncase, String description ) {
        getCase( ncase ).setDescription( description );
        return self();
    }

    public SELF all_cases_have_a_step_$_with_argument( String name, String arg ) {
        int i = 1;
        for( ScenarioCaseModel caseModel : reportModel.getLastScenarioModel().getScenarioCases() ) {
            case_$_has_a_step_$_with_argument( i++, name, arg );
        }
        return self();
    }

    public SELF case_$_has_step_$( int ncase, String name ) {
        getCase( ncase ).addStep( new StepModel( name, Arrays.asList( Word.introWord( "when" ), new Word( name ) ) ) );
        return self();
    }

    public SELF case_$_has_a_step_$_with_argument( int i, String name, String arg ) {
        return case_$_has_a_when_step_$_with_argument( i, name, arg );
    }

    private ScenarioCaseModel getCase( int scenarioNr, int caseNr ) {
        return reportModel.getScenarios().get( scenarioNr - 1 ).getCase( caseNr - 1 );
    }

    private ScenarioCaseModel getCase( int ncase ) {
        return reportModel.getLastScenarioModel().getScenarioCases().get( ncase - 1 );
    }

    public SELF step_$_is_named( int i, String name ) {
        getCase( 1 ).getStep( i - 1 ).getWords().get( 1 ).setValue( name );
        return self();
    }

    public SELF step_$_of_case_$_has_status( int stepNr, int caseNr, StepStatus status ) {
        getCase( caseNr ).getStep( stepNr - 1 ).setStatus( status );
        return self();
    }

    public SELF step_$_has_status( int stepNr, StepStatus status ) {
        return step_$_of_case_$_has_status( stepNr, 1, status );
    }

    public SELF step_$_has_a_duration_of_$_nano_seconds( int i, long durationInNanos ) {
        getCase( 1 ).getStep( i - 1 ).setDurationInNanos( durationInNanos );
        return self();
    }

    public SELF case_$_has_a_when_step_$_with_argument( int ncase, String name, String arg ) {
        return case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( ncase, name, arg, "argName" );
    }

    public SELF case_$_has_a_when_step_$_with_argument_$_and_argument_name_$( int ncase, @Quoted String name, @Quoted String arg,
            @Quoted String argName ) {
        lastArgWord = Word.argWord( argName, arg, arg );
        getCase( ncase )
                .addStep(
                        new StepModel( name,
                                Arrays.asList( Word.introWord( "when" ), new Word( name ), lastArgWord ) ) );
        return self();
    }

    public SELF formatted_value( @Quoted String formattedValue ) {
        lastArgWord.getArgumentInfo().setFormattedValue( formattedValue );
        return self();
    }

    public SELF the_first_scenario_has_tag( @Quoted String name ) {
        return scenario_$_has_tag_$_with_value_$( 1, name, null );
    }

    public SELF scenario_$_has_tag_$_with_value_$( int i, String name, String value ) {
        latestTag = new Tag( name, value ).setPrependType( true );
        reportModel.getScenarios().get( i - 1 ).addTag( latestTag );
        reportModel.addTag( latestTag );
        return self();
    }

    public void the_tag_has_prependTpe_set_to( boolean prependType ) {
        latestTag.setPrependType( prependType );
    }

    public SELF the_tag_has_style( String style ) {
        latestTag.setStyle( style );
        return self();
    }

    @AfterStage
    public void analyzeReport() {
        if( analyze ) {
            new CaseArgumentAnalyser().analyze( reportModel );
        }
    }

    public void transpose_set_to( boolean b ) {
    }

    public SELF header_type_set_to( Table.HeaderType headerType ) {
        latestWord.getArgumentInfo().getDataTable().setHeaderType( headerType );
        return self();
    }

    public SELF step_$_of_scenario_$_has_an_attachment_with_content( int stepNr, int scenarioNr, String content ) {
        StepModel step = getStep( stepNr, scenarioNr );
        step.addAttachment( Attachment.fromText( content, MediaType.PLAIN_TEXT_UTF_8 ) );
        return self();
    }

    public SELF step_$_of_case_$_has_an_attachment_with_content( int stepNr, int caseNr, String content ) {
        StepModel step = getStep( stepNr, 1, caseNr );
        step.addAttachment( Attachment.fromText( content, MediaType.PLAIN_TEXT_UTF_8 ) );
        return self();
    }

    public SELF step_$_of_scenario_$_has_another_attachment_with_content( int stepNr, int scenarioNr, String content ) {
        return step_$_of_scenario_$_has_an_attachment_with_content( stepNr, scenarioNr, content );
    }

    private StepModel getStep( int stepNr, int scenarioNr ) {
        return getStep( stepNr, scenarioNr, 1 );
    }

    private StepModel getStep( int stepNr, int scenarioNr, int caseNr ) {
        return reportModel.getScenarios().get( scenarioNr - 1 ).getScenarioCases().get( caseNr - 1 ).getStep( stepNr - 1 );
    }

    public SELF a_step_has_a_data_table_with_following_values( @Table List<List<String>> dataTable ) {
        return step_$_of_scenario_$_has_a_data_table_as_parameter( dataTable );
    }

    public SELF step_$_of_scenario_$_has_a_data_table_as_parameter( @Table List<List<String>> dataTable ) {
        StepModel step = getStep( 1, 1 );
        Word word = Word.argWord( "a", "b", new DataTable( Table.HeaderType.HORIZONTAL, dataTable ) );
        step.addWords( word );
        latestWord = word;
        return self();
    }

    public SELF case_$_has_no_steps( int caseNr ) {
        reportModel.getLastScenarioModel().getCase( caseNr - 1 ).setSteps( Collections.<StepModel>emptyList() );
        return self();
    }

    public SELF scenario_$_has_no_steps( int i ) {
        ScenarioModel scenarioModel = reportModel.getLastScenarioModel();
        for( ScenarioCaseModel caseModel : scenarioModel.getScenarioCases() ) {
            caseModel.setSteps( Collections.<StepModel>emptyList() );
        }
        return self();
    }

}
