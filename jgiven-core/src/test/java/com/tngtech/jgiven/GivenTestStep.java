package com.tngtech.jgiven;

import java.lang.reflect.Method;

import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.AsProvider;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Formatf;
import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.format.BooleanFormatter;

public class GivenTestStep extends Stage<GivenTestStep> {

    @ProvidedScenarioState
    int value1;

    @ProvidedScenarioState
    int value2;

    @ScenarioStage
    GivenTestComposedStep givenTestComposedStep;

    public void some_integer_value( int someIntValue ) {
        value1 = someIntValue;
    }

    public void another_integer_value( int anotherValue ) {
        value2 = anotherValue;
    }

    public void an_integer_value_set_in_a_substep( int substepValue ) {
        givenTestComposedStep.some_integer_value_in_the_substep( substepValue );
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
            throw new RuntimeException( "failure" );
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
        return given().something().and().something_else_that_fails().and().something_else();
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

    @As( value = "output", provider = CustomAsProvider.class )
    public GivenTestStep a_step_with_an_As_annotation_and_a_custom_provider() {
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

    public GivenTestStep ALLUPPERCASE() {
        return self();
    }

    @As( "another description" )
    @IntroWord
    public GivenTestStep an_intro_word_with_an_as_annotation() {
        return self();
    }

    public static class CustomAsProvider implements AsProvider {

        @Override
        public String as( As annotation, Method method ) {
            return "Custom AsProvider " + annotation.value() + ": " + method.getName();
        }

        @Override
        public String as( As annotation, Class<?> scenarioClass ) {
            return null;
        }

    }

}
