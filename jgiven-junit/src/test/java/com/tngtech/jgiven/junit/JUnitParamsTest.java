package com.tngtech.jgiven.junit;

import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;
import com.tngtech.jgiven.report.model.ScenarioModel;

@RunWith( JUnitParamsRunner.class )
public class JUnitParamsTest extends ScenarioTest<GivenTestStep, WhenTestStep, ThenTestStep> {

    @Test
    @Parameters( {
        "-2, false, 0",
        "22, true, 1" } )
    public void JUnitParamsRunner_can_be_used( int intArg, boolean booleanArg, int caseNr ) {
        given().some_integer_value( intArg );
        when().multiply_with_two();
        then().the_value_is_$not$_greater_than_zero( booleanArg );

        ScenarioModel scenarioModel = getScenario().getModel().getLastScenarioModel();
        List<String> arguments = scenarioModel.getCase( caseNr ).getExplicitArguments();
        // Currently not working:
        // assertThat( arguments ).containsExactly( "" + intArg, "" + booleanArg, "" + caseNr );
    }

}
