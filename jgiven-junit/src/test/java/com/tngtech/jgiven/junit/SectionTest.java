package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;

@Description( "Scenarios can have sections" )
public class SectionTest extends ScenarioTest<GivenTestStep, WhenTestStep, ThenTestStep> {

    @Test
    public void scenarios_can_have_sections() throws Throwable {
        section( "This is a section" );
        given().some_boolean_value( true );
        when().something();
        section( "And this is another section" );
        given().some_integer_value( 5 );
        when().something();

        getScenario().finished();

        ScenarioCaseModel aCase = getScenario().getModel().getLastScenarioModel().getCase( 0 );
        assertThat( aCase.getSteps() ).hasSize( 6 );
        assertThat( aCase.getStep( 0 ).isSectionTitle() ).isTrue();
        assertThat( aCase.getStep( 3 ).isSectionTitle() ).isTrue();
    }

}
