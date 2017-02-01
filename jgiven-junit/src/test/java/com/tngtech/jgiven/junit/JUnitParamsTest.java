package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.annotation.OrdinalCaseDescription;
import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith( JUnitParamsRunner.class )
public class JUnitParamsTest extends ScenarioTest<GivenTestStep, WhenTestStep, ThenTestStep> {

    @Test
    @Parameters( {
            "-2, false, 0",
            "22, true, 1" } )
    public void JUnitParamsRunner_can_be_used( int intArg, boolean booleanArg, int caseNr ) {
        given().some_integer_value( intArg );
        when().multiply_with_two();
        then().the_value_is_$not_greater_than_zero( booleanArg );

        ScenarioCaseModel scenarioModel = getScenario().getScenarioCaseModel();
        List<String> arguments = scenarioModel.getExplicitArguments();
        assertThat( arguments ).containsExactly( "" + intArg, "" + booleanArg, "" + caseNr );
    }

    @Test
    @Parameters( {
            "-2, false, 0",
            "22, true, 1" } )
    @OrdinalCaseDescription( "Hi there $3" )
    public void case_names_are_used_as_description( int intArg, boolean booleanArg, int caseNr ) {
        given().something();

        ScenarioCaseModel scenarioModel = getScenario().getScenarioCaseModel();
        assertThat( scenarioModel.getDescription() ).isEqualTo( "Hi there " + caseNr );
    }

}
