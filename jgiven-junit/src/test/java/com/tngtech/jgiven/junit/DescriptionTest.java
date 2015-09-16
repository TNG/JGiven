package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;

@Description( "Some description for the test class" )
public class DescriptionTest extends ScenarioTest<GivenTestStep, WhenTestStep, ThenTestStep> {

    @Test
    public void descriptions_on_test_classes_are_evaluated() throws Throwable {
        given().some_boolean_value( true );

        getScenario().finished();

        assertThat( getScenario().getModel().getDescription() ).isEqualTo( "Some description for the test class" );
    }

    @Test
    @ExtendedDescription( "Scenarios can have extended descriptions" )
    public void extended_descriptions_on_test_methods_are_evaluated() throws Throwable {
        given().some_boolean_value( true );

        getScenario().finished();

        assertThat( getScenario().getScenarioModel().getExtendedDescription() ).isEqualTo( "Scenarios can have extended descriptions" );
    }

}
