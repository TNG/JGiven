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
import com.tngtech.jgiven.report.impl.CaseArgumentAnalyser;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.Word;

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
        given().some_integer_value( intArg )
            .and().some_boolean_value( booleanArg );
        when().multiply_with_two();
        then().the_value_is_$not$_greater_than_zero( booleanArg );

        ScenarioModel scenarioModel = getScenario().getModel().scenarios.get( 0 );
        List<String> arguments = scenarioModel.getCase( caseNr ).arguments;
        assertThat( arguments ).containsExactly( "" + intArg, "" + booleanArg, "" + caseNr );
    }

    @DataProvider
    public static Object[][] trickyData() {
        return new Object[][] {
            { 0, 0, 0 },
            { 0, 1, 0 },
            { 0, 0, 1 },
        };
    }

    @Test
    @UseDataProvider( "trickyData" )
    public void DataProviderRunner_with_tricky_data( int firstArg, int secondArg, int thirdArg ) {
        given().some_integer_value( firstArg )
            .and().another_integer_value( secondArg )
            .and().a_third_integer_value( thirdArg );

        when().multiply_with_two();

        ScenarioModel scenarioModel = getScenario().getModel().scenarios.get( 0 );
        if( scenarioModel.getScenarioCases().size() == 3 ) {
            CaseArgumentAnalyser analyser = new CaseArgumentAnalyser();
            analyser.analyze( scenarioModel );
            ScenarioCaseModel case0 = scenarioModel.getCase( 0 );
            assertParamIndex( case0, 0, 0 );
        }
    }

    private void assertParamIndex( ScenarioCaseModel case0, int step, int parameterIndex ) {
        Word word = case0.getStep( step ).words.get( 2 );
        assertThat( word.getArgumentInfo().getParameterIndex() ).isEqualTo( parameterIndex );
    }

}
