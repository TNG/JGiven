package com.tngtech.jgiven.junit.injection;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ComposedScenarioStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MultilevelComposedStepInjectionTest extends ScenarioTest {

    @ScenarioStage
    CustomerSteps customerSteps;

    @ScenarioStage
    ThenCustomer thenCustomer;

    @Test
    public void substeps_are_injected_into_test_case() {
        customerSteps.given().a_customer();
        thenCustomer.then().the_site_language_is( "de-DE" );
    }


    static class CustomerSteps extends Stage<CustomerSteps> {

        @ComposedScenarioStage
        LanguageSteps languageSteps;

        public CustomerSteps a_customer() {
            languageSteps.the_site_language_is_set_to( "de-DE" );
            return self();
        }

    }

    static class LanguageSteps extends Stage<LanguageSteps> {


        @ComposedScenarioStage
        OtherLanguageSteps otherLanguageSteps;

        public LanguageSteps the_site_language_is_set_to( String language ) {
            otherLanguageSteps.the_site_language_is_set_to( language );
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


        public ThenCustomer the_site_language_is( String expectedLanguage ) {
            assertThat( language ).isEqualTo( expectedLanguage );
            assertThat( languageSteps.otherLanguageSteps.language ).isEqualTo( expectedLanguage );
            assertThat( otherLanguageSteps.language ).isEqualTo( expectedLanguage );

            return self();
        }

    }

    static class OtherLanguageSteps extends Stage<OtherLanguageSteps> {

        @ExpectedScenarioState
        String language;

        public OtherLanguageSteps the_site_language_is_set_to(String language) {
            this.language = language;
            return this;
        }


    }

    static class Customer {
    }

}
