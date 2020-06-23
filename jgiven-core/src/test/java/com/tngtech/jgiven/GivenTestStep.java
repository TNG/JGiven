package com.tngtech.jgiven;

import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.AsProvider;
import com.tngtech.jgiven.annotation.FillerWord;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Formatf;
import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.annotation.NestedSteps;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.format.BooleanFormatter;

import java.lang.reflect.Method;

public class GivenTestStep extends Stage<GivenTestStep> {

    @ProvidedScenarioState
    int value1;

    @ProvidedScenarioState
    int value2;

    @ScenarioStage
    GivenTestComposedStep givenTestComposedStep;

    @ScenarioState
    CurrentStep currentStep;

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

    @FillerWord
    public GivenTestStep something_filled() {
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

    public GivenTestStep a_step_that_sets_the_name() {
        this.currentStep.setName("another name");
        return self();
    }

    public GivenTestStep a_step_that_sets_a_comment() {
        this.currentStep.setComment("a comment");
        return self();
    }

    public GivenTestStep a_step_that_sets_the_name_with_an_argument(String argument) {
        this.currentStep.setName("another name " + argument);
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

    public GivenTestStep varargs_as_parameters_$( String... params ) {
        return self();
    }

    public GivenTestStep ALLUPPERCASE() {
        return self();
    }

    public GivenTestStep arrays_as_parameters(String[] params) {
        return self();
    }

    public GivenTestStep table_as_parameter(@Table String[] params) {
        return self();
    }

    @IntroWord
    @As( "another description" )
    public GivenTestStep an_intro_word_with_an_as_annotation() {
        return self();
    }

    @FillerWord
    public GivenTestStep a() {
        return self();
    }

    @FillerWord
    public GivenTestStep and_with() {
        return self();
    }

    @FillerWord
    public GivenTestStep another() {
        return self();
    }

    @FillerWord
    public GivenTestStep there() {
        return self();
    }

    @FillerWord
    public GivenTestStep is() {
        return self();
    }

    @FillerWord
    @As( "Filler Word" )
    public GivenTestStep filler_word_with_an_as_annotation() {
        return self();
    }

    @As(",")
    @FillerWord(joinToPreviousWord = true)
    public GivenTestStep comma() {
        return self();
    }

    @As(":")
    @FillerWord(joinToPreviousWord = true)
    public GivenTestStep colon() {
        return self();
    }

    @As(".")
    @FillerWord(joinToPreviousWord = true)
    public GivenTestStep full_stop() {
        return self();
    }

    @As("(")
    @FillerWord(joinToNextWord = true)
    public GivenTestStep open_bracket() {
        return self();
    }

    @As(")")
    @FillerWord(joinToPreviousWord = true)
    public GivenTestStep close_bracket() {
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
