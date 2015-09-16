package com.tngtech.jgiven.examples.description;

import org.junit.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

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

    }

}
