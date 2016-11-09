package com.tngtech.jgiven.report.model;

import java.util.List;

import com.tngtech.jgiven.relocated.guava.collect.Lists;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;

public class GivenReportModels<SELF extends GivenReportModels<?>> extends Stage<SELF> {
    @ExpectedScenarioState
    protected ReportModel reportModel;

    @ProvidedScenarioState
    protected List<ReportModel> reportModels = Lists.newArrayList();

    @ScenarioStage
    public GivenReportModel<?> givenReportModel;

    @ProvidedScenarioState
    private Attachment attachment;

    @BeforeStage
    public void useReportModel() {
        if( reportModel != null ) {
            reportModels.add( reportModel );
        }
    }

    public SELF $_report_models( int n ) {
        for( int i = 0; i < n; i++ ) {
            a_report_model_with_name( "Test" + i );
        }

        return self();
    }

    public SELF a_report_model() {
        return a_report_model_with_name( "Test" );
    }

    public SELF a_report_model_with_name( String name ) {
        ReportModel reportModel = givenReportModel.a_report_model_with_name( name ).getReportModel();
        reportModels.add( reportModel );
        return self();
    }

    public SELF the_report_has_$_scenarios( int n ) {
        givenReportModel.the_report_has_$_scenarios( n );
        return self();
    }

    public SELF the_first_scenario_has_tag( String name ) {
        givenReportModel.the_first_scenario_has_tag( name );
        return self();
    }

    public SELF the_tag_has_style( String style ) {
        givenReportModel.the_tag_has_style( style );
        return self();
    }

    public SELF scenario_$_has_tag_$_with_value_$( int i, String name, String value ) {
        givenReportModel.scenario_$_has_tag_$_with_value_$( i, name, value );
        return self();
    }

    public SELF the_tag_has_prependType_set_to( boolean prependType ) {
        givenReportModel.the_tag_has_prependTpe_set_to( prependType );
        return self();
    }

    public SELF case_$_of_scenario_$_has_failed( int caseNr, int scenarioNr ) {
        givenReportModel.case_$_of_scenario_$_has_failed( caseNr, scenarioNr );
        return self();
    }

    public SELF the_scenario_has_one_parameter() {
        givenReportModel.the_scenario_has_parameters( "foo" );
        return self();
    }

    public SELF the_scenario_has_parameters( String... params ) {
        givenReportModel.the_scenario_has_parameters( params );
        return self();
    }

    public SELF the_scenario_has_$_default_cases( int ncases ) {
        givenReportModel.the_scenario_has_$_default_cases( ncases );
        return self();
    }

    public SELF step_$_of_case_$_has_status( int stepNr, int caseNr, StepStatus status ) {
        givenReportModel.step_$_of_case_$_has_status( stepNr, caseNr, status );
        return self();
    }

    public SELF step_$_of_case_$_has_a_text_attachment( int stepNr, int caseNr ) {
        givenReportModel.step_$_of_case_$_has_an_attachment_with_content_and_media_type( stepNr, caseNr, "Test Text" );
        return self();
    }

    public SELF step_$_of_case_$_has_a_text_attachment_with_content_$_and_mediaType( int stepNr, int caseNr, String content,
            MediaType mediaType ) {
        givenReportModel.step_$_of_case_$_has_an_attachment_with_content_and_media_type( stepNr, caseNr, content, mediaType );
        return self();
    }

    public SELF step_$_of_case_$_has_a_text_attachment_with_content( int stepNr, int caseNr, String content ) {
        givenReportModel.step_$_of_case_$_has_an_attachment_with_content_and_media_type( stepNr, caseNr, content );
        return self();
    }

    public SELF step_$_of_scenario_$_has_a_text_attachment_with_content( int stepNr, int scenarioNr, String content ) {
        givenReportModel.step_$_of_scenario_$_has_an_attachment_with_content( stepNr, scenarioNr, content );
        return self();
    }

    public SELF step_$_of_scenario_$_has_another_text_attachment_with_content( int stepNr, int scenarioNr, String content ) {
        givenReportModel.step_$_of_scenario_$_has_another_attachment_with_content( stepNr, scenarioNr, content );
        return self();
    }

    public SELF scenario_$_has_no_steps( int i ) {
        givenReportModel.scenario_$_has_no_steps( i );
        return self();
    }

    public SELF step_$_of_case_$_has_a_formatted_value_$_as_parameter( int stepNr, int caseNr, String formattedValue ) {
        givenReportModel.step_$_of_case_$_has_a_formatted_value_$_as_parameter( stepNr, caseNr, formattedValue );
        return self();
    }

    public SELF the_attachment_is_added_to_step_$_of_case_$( int stepNr, int caseNr ) {
        givenReportModel.the_attachment_is_added_to_step_$_of_case_$( stepNr, caseNr );
        return self();
    }

    public SELF the_attachments_are_added_to_step_$_of_case_$(int stepNr, int caseNr) {
        givenReportModel.the_attachments_are_added_to_step_$_of_case_$(stepNr, caseNr);
        return self();
    }
}
