package com.tngtech.jgiven.examples.description;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( JUnitParamsRunner.class )
@Description( "Demonstrates the usage of the @As annotation" )
public class AsAnnotationExampleTest extends SimpleScenarioTest<AsAnnotationExampleTest.AsAnnotationStage> {

    @Test
    @As( "Scenario that shows the usage of the @As annotation" )
    public void scenario_that_shows_how_to_override_the_default_text_of_a_step() {
        given().something()
                .comma().something();
    }

    @Test
    @ExtendedDescription( "This scenario has a very long <tt>@ExtendedDescription</tt>. " +
            "Extended descriptions can give additional information about the rational of a scenario. You can even use <b>HTML</b>." )
    public void scenarios_can_have_an_extended_description() {
        given().something()
                .and().something();
    }

    @Test
    @As( "Scenario that shows the usage of @As with argument enumeration" )
    @Parameters( {
            "false, 0"
    } )
    public void steps_can_use_at_annotation_to_reference_arguments_by_enumeration( boolean bool, int i ) {
        given().some_boolean_$_and_int_$_value( bool, i );
    }

    @Test
    @As( "Scenario that shows the usage of @As with argument names" )
    @Parameters( {
            "true, 1"
    } )
    public void steps_can_use_at_annotation_to_reference_arguments_by_name( boolean bool, int i ) {
        given().argument_names_can_be_references( bool, i );
    }

    public static class AsAnnotationStage extends Stage<AsAnnotationStage> {

        @IntroWord
        @As( "," )
        public AsAnnotationStage comma() {
            return this;
        }

        @As( "something else" )
        public AsAnnotationStage something() {
            return this;
        }

        @As( "the reference to the first argument : $1 and the second argument : $2 " )
        public AsAnnotationStage some_boolean_$_and_int_$_value( boolean bool, int i ) {
            return this;
        }

        @As( "the reference to the second argument : $i and the first argument : $bool " )
        public AsAnnotationStage argument_names_can_be_references( boolean bool, int i ) {
            return this;
        }

    }

}
