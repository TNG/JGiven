package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.config.AbstractJGivenConfiguration;
import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;

@As( "Some other name" )
public class TestClassNameTest extends ScenarioTest<GivenTestStep, WhenTestStep, ThenTestStep> {

    @Test
    public void classes_can_be_given_names_with_the_As_annotation() throws Throwable {
        given().some_boolean_value( true );

        getScenario().finished();

        assertThat( getScenario().getModel().getName() ).isEqualTo( "Some other name" );
    }

    public static class CustomTestSuffixConfiguration extends AbstractJGivenConfiguration {
        @Override
        public void configure() {
            setTestClassSuffixRegEx( "ConfigurationTest" );
        }
    }
}
