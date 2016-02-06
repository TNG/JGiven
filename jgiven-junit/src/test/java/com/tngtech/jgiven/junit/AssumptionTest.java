package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.AssumptionViolatedException;
import org.junit.Test;

import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.StepStatus;

@Description( "Scenarios can have sections" )
public class AssumptionTest extends ScenarioTest<GivenTestStep, WhenTestStep, ThenTestStep> {

    @Test
    public void JUnit_assumption_exceptions_should_be_treated_correctly() throws Throwable {
        try {
            when().some_assumption_fails();
            Assertions.fail( "AssumptionViolationException should have been thrown" );
        } catch( AssumptionViolatedException e ) {}

        getScenario().finished();

        ScenarioCaseModel aCase = getScenario().getModel().getLastScenarioModel().getCase( 0 );
        assertThat( aCase.getStep( 0 ).getStatus() ).isEqualTo( StepStatus.PASSED );
    }

}
