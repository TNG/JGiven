package com.tngtech.jgiven.junit.injection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit.ScenarioTest;

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

        @ScenarioStage
        LanguageSteps languageSteps;

        public CustomerSteps a_customer() {
            languageSteps.the_site_language_is_set_to( "de-DE" );
            return self();
        }

    }

    static class LanguageSteps extends Stage<LanguageSteps> {

        @ScenarioStage
        OtherLanguageSteps otherLanguageSteps;

        public LanguageSteps the_site_language_is_set_to( String language ) {
            otherLanguageSteps.the_site_language_is_set_to( language );
            return this;
        }

        public void the_site_language_is( String expectedLanguage ) {
            otherLanguageSteps.the_site_language_is( expectedLanguage );
        }

    }

    static class ThenCustomer extends Stage<ThenCustomer> {

        @ExpectedScenarioState
        String language;

        @ScenarioStage
        LanguageSteps languageSteps;

        @ScenarioStage
        OtherLanguageSteps otherLanguageSteps;

        public ThenCustomer the_site_language_is( String expectedLanguage ) {
            assertThat( language ).isEqualTo( expectedLanguage );
            languageSteps.the_site_language_is( expectedLanguage );
            otherLanguageSteps.the_site_language_is( expectedLanguage );

            return self();
        }

    }

    static class OtherLanguageSteps extends Stage<OtherLanguageSteps> {

        @ExpectedScenarioState
        String language;

        public OtherLanguageSteps the_site_language_is_set_to( String language ) {
            this.language = language;
            return this;
        }

        public void the_site_language_is( String expectedLanguage ) {
            assertThat( language ).isEqualTo( expectedLanguage );
        }

    }

    static class Customer {}

}
