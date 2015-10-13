package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.config.AbstractJGivenConfiguration;
import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;

@JGivenConfiguration( TestClassSuffixConfigurationTest.CustomTestSuffixConfiguration.class )
public class TestClassSuffixConfigurationTest extends ScenarioTest<GivenTestStep, WhenTestStep, ThenTestStep> {

    @Test
    public void class_name_suffix_can_be_configured() throws Throwable {
        given().some_boolean_value( true );

        getScenario().finished();

        assertThat( getScenario().getModel().getName() ).isEqualTo( "Test Class Suffix" );
    }

    public static class CustomTestSuffixConfiguration extends AbstractJGivenConfiguration {
        @Override
        public void configure() {
            setTestClassSuffixRegEx( "ConfigurationTest" );
        }
    }
}
