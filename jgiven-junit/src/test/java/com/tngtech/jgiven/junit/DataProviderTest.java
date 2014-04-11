package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;
import com.tngtech.jgiven.report.model.ScenarioModel;

@RunWith( DataProviderRunner.class )
public class DataProviderTest extends ScenarioTest<GivenTestStep, WhenTestStep, ThenTestStep> {

    @DataProvider
    public static Object[][] dataProvider() {
        return new Object[][] {
            { -2, false, 0 },
            { 22, true, 1 } };
    }

    @Test
    @UseDataProvider( "dataProvider" )
    public void DataProviderRunner_can_be_used( int intArg, boolean booleanArg, int caseNr ) {
        given().some_integer_value( intArg );
        when().multiply_with_two();
        then().the_value_is_$not$_greater_than_zero( booleanArg );

        ScenarioModel scenarioModel = scenario.getModel().scenarios.get( 0 );
        List<String> arguments = scenarioModel.scenarioCases.get( caseNr ).arguments;
        assertThat( arguments ).containsExactly( "" + intArg, "" + booleanArg, "" + caseNr );
    }

}
