package com.tngtech.jgiven.junit.injection;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.annotation.ComposedScenarioStage;
import com.tngtech.jgiven.junit.ScenarioTest;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ComposedStepInjectionTest extends ScenarioTest {

    @ScenarioStage
    CustomerSteps customerSteps;

    @ScenarioStage
    ThenCustomer thenCustomer;

    @Test
    public void substeps_are_injected_into_test_case() {
        customerSteps.given().a_customer()
            .and().the_default_language_is_set()
            .and().the_default_colour_is_set();
        thenCustomer.then().the_site_language_is( "de-DE" )
            .and().the_site_color_is( "Red" );
    }


    static class CustomerSteps extends Stage<CustomerSteps> {

        @ComposedScenarioStage
        LanguageSteps languageSteps;

        @ComposedScenarioStage
        ColorSteps colorSteps;

        @ScenarioState
        String language;

        @ScenarioState
        String color;

        public CustomerSteps a_customer() {
            languageSteps.the_site_language_is_set_to( "de-DE" );
            colorSteps.the_site_color_is_set_to( "Red" );
            return self();
        }

        public CustomerSteps the_default_language_is_set() {
            assertThat( language ).isEqualTo( "de-DE" );
            return self();
        }

        public CustomerSteps the_default_colour_is_set() {
            assertThat( color ).isEqualTo( "Red" );
            return self();
        }

    }

    static class LanguageSteps extends Stage<LanguageSteps> {

        @ScenarioState
        String language;

        public LanguageSteps the_site_language_is_set_to( String language ) {
            this.language = language;
            return this;
        }

    }

    static class ColorSteps extends Stage<ColorSteps> {

        @ScenarioState
        String color;

        public ColorSteps the_site_color_is_set_to( String color ) {
            this.color = color;
            return this;
        }

    }

    static class ThenCustomer extends Stage<ThenCustomer> {

        @ExpectedScenarioState
        String language;

        @ComposedScenarioStage
        LanguageSteps languageSteps;

        @ComposedScenarioStage
        OtherLanguageSteps otherLanguageSteps;

        @ExpectedScenarioState
        String color;


        public ThenCustomer the_site_language_is( String expectedLanguage) {
            assertThat( language ).isEqualTo( expectedLanguage );
            assertThat( languageSteps.language ).isEqualTo( expectedLanguage );
            assertThat( otherLanguageSteps.language ).isEqualTo (expectedLanguage );

            return self();
        }

        public ThenCustomer the_site_color_is( String expectedSiteColor ) {
            assertThat( color ).isEqualTo( expectedSiteColor );
            return self();
        }
    }

    static class OtherLanguageSteps extends Stage<OtherLanguageSteps> {

        @ExpectedScenarioState
        String language;

    }


}
