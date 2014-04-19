package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.annotation.NotImplementedYet;
import com.tngtech.jgiven.annotation.ScenarioDescription;
import com.tngtech.jgiven.junit.StepsAreReported.TestSteps;
import com.tngtech.jgiven.report.model.ImplementationStatus;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Word;

@RunWith( DataProviderRunner.class )
public class StepsAreReported extends ScenarioTest<TestSteps, TestSteps, TestSteps> {

    @Test
    public void given_steps_are_reported() {

        given().some_test_step();

        getScenario().finished();
        ScenarioModel model = getScenario().getModel().getLastScenarioModel();

        assertThat( model.className ).isEqualTo( StepsAreReported.class.getName() );
        assertThat( model.testMethodName ).isEqualTo( "given_steps_are_reported" );
        assertThat( model.description ).isEqualTo( "given steps are reported" );
        assertThat( model.parameterNames ).isEmpty();
        assertThat( model.tags ).isEmpty();
        assertThat( model.getScenarioCases() ).hasSize( 1 );

        ScenarioCaseModel scenarioCase = model.getCase( 0 );
        assertThat( scenarioCase.arguments ).isEmpty();
        assertThat( scenarioCase.caseNr ).isEqualTo( 1 );
        assertThat( scenarioCase.steps ).hasSize( 1 );

        StepModel step = scenarioCase.steps.get( 0 );
        assertThat( step.name ).isEqualTo( "some test step" );
        assertThat( step.words ).isEqualTo( Arrays.asList( new Word( "Given" ), new Word( "some test step" ) ) );
        assertThat( step.notImplementedYet ).isFalse();

    }

    @Test
    public void steps_annotated_with_NotImplementedYet_are_recognized() {
        given().some_not_implemented_step();

        getScenario().finished();

        ScenarioModel model = getScenario().getModel().getLastScenarioModel();
        StepModel stepModel = model.getCase( 0 ).steps.get( 0 );
        assertThat( stepModel.notImplementedYet ).isTrue();
        assertThat( model.getImplementationStatus() ).isEqualTo( ImplementationStatus.NONE );
    }

    @Test
    public void if_some_steps_are_not_implemented_then_scenario_status_is_partially() {
        given().some_test_step();
        given().some_not_implemented_step();

        getScenario().finished();

        ScenarioModel model = getScenario().getModel().getLastScenarioModel();
        assertThat( model.getImplementationStatus() ).isEqualTo( ImplementationStatus.PARTIALLY );
    }

    @Test
    public void if_some_step_fails_then_the_error_message_is_stored() {
        try {
            given().a_step_fails();
        } catch( AssertionError e ) {

        }
        ScenarioCaseModel model = getScenario().getModel().getLastScenarioModel().getCase( 0 );
        assertThat( model.errorMessage ).isNotNull();
    }

    @Retention( RetentionPolicy.RUNTIME )
    @IsTag( explodeArray = false )
    public @interface TestTag {
        String[] value();
    }

    @Test
    @TestTag( { "foo", "bar", "baz" } )
    public void annotations_are_translated_to_tags() {
        given().some_test_step();
        getScenario().finished();

        ScenarioModel model = getScenario().getModel().getLastScenarioModel();
        assertThat( model.tags ).hasSize( 1 );

        assertThat( model.getTags().get( 0 ).getName() ).isEqualTo( "TestTag" );
        assertThat( model.getTags().get( 0 ).getValue() ).isEqualTo( new String[] { "foo", "bar", "baz" } );
    }

    @DataProvider
    public static Object[][] testValues() {
        return new Object[][] { { 1 }, { 2 } };
    }

    @Test
    @TestTag( { "foo", "bar", "baz" } )
    @UseDataProvider( "testValues" )
    public void annotations_are_translated_to_tags_only_once( int n ) {
        given().some_test_step();
        getScenario().finished();

        ScenarioModel model = getScenario().getModel().getLastScenarioModel();
        assertThat( model.tags ).hasSize( 1 );

        assertThat( model.getTags().get( 0 ).getName() ).isEqualTo( "TestTag" );
        assertThat( model.getTags().get( 0 ).getValue() ).isEqualTo( new String[] { "foo", "bar", "baz" } );
    }

    @Test
    @ScenarioDescription( "Some other description" )
    public void ScenarioDescription_annotation_is_evaluated() {
        given().some_test_step();
        getScenario().finished();
        assertThat( getScenario().getModel().getLastScenarioModel().description ).isEqualTo( "Some other description" );
    }

    public static class TestSteps extends Stage<TestSteps> {
        public void some_test_step() {

        }

        @NotImplementedYet
        public void some_not_implemented_step() {

        }

        public void a_step_fails() {
            assertThat( true ).isFalse();
        }
    }

}
