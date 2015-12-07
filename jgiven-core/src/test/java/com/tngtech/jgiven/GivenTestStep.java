package com.tngtech.jgiven;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.format.BooleanFormatter;

public class GivenTestStep extends Stage<GivenTestStep> {

    @ProvidedScenarioState
    int value1;

    @ProvidedScenarioState
    int value2;

    public void some_integer_value( int someIntValue ) {
        this.value1 = someIntValue;
    }

    public void another_integer_value( int anotherValue ) {
        this.value2 = anotherValue;
    }

    public void $d_and_$d( int value1, int value2 ) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public GivenTestStep something() {
        return self();
    }

    public GivenTestStep something_else() {
        return self();
    }

    public GivenTestStep something_further() {
        return self();
    }

    public GivenTestStep something_else_that_fails() {
        if( 1 == 1 ) {
            throw new RuntimeException("failure");
        }
        return self();
    }

    @NestedSteps
    public GivenTestStep something_with_nested_steps() {
        return given().something().and().something_else();
    }

    @NestedSteps
    public GivenTestStep something_with_multilevel_nested_steps() {
        return given().something_with_nested_steps().and().something_further();
    }

    @NestedSteps
    public GivenTestStep something_with_nested_steps_that_fails() {
        return given().something().and().something_else_that_fails();
    }


    public GivenTestStep an_array( Object argument ) {
        return self();
    }

    @As( "a step with a (special) description" )
    public GivenTestStep a_step_with_a_description() {
        return self();
    }

    public GivenTestStep aStepInCamelCase() {
        return self();
    }



    @As( "a step with a bracket after a dollar $]" )
    public GivenTestStep a_step_with_a_bracket_after_a_dollar( int value ) {
        return self();
    }

    public GivenTestStep a_step_with_a_printf_annotation_$( @Formatf( "%.2f" ) double d ) {
        return self();
    }

    public GivenTestStep a_step_with_a_$_parameter( String param ) {
        return self();
    }

    public GivenTestStep a_step_with_a_boolean_$_parameter( @Format( value = BooleanFormatter.class, args = { "yes", "no" } ) boolean b ) {
        return self();
    }

    public GivenTestStep aStepInCamelCaseWithA$Parameter( String param ) {
        return self();
    }

    @As( "another description" )
    @IntroWord
    public GivenTestStep an_intro_word_with_an_as_annotation() {
        return self();
    }
}
