package com.tngtech.jgiven.testng;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Word;
import com.tngtech.jgiven.testng.TestNgTest.TestSteps;

public class ParameterizedTestNgTest extends ScenarioTest<TestSteps, TestSteps, TestSteps> {

    @DataProvider
    public static Object[][] parameters() {
        return new Object[][] {
            { 5, "foo", 0 },
            { 42, "bar", 1 }
        };
    }

    @Test( dataProvider = "parameters" )
    public void parameters_are_handled_correctly( int milkInLiter, String ingredient, int caseNr ) {
        parametersAreHandledCorrectly( "parameters are handled correctly", milkInLiter, ingredient, caseNr );
    }

    @DataProvider( parallel = true )
    public static Object[][] parallelParameters() {
        return new Object[][] {
            { 5, "foo", 0 },
            { 42, "bar", 1 }
        };
    }

    @Test( dataProvider = "parallelParameters" )
    public void parallel_data_providers_are_handled_correctly( int milkInLiter, String ingredient, int caseNr ) {
        parametersAreHandledCorrectly( "parallel data providers are handled correctly", milkInLiter, ingredient, caseNr );
    }

    private void parametersAreHandledCorrectly( String title, int milkInLiter, String ingredient, int caseNr ) {
        given().$_l_milk( milkInLiter )
            .and().ingredient( ingredient );
        when().mixed_with( "something" );
        then().nothing_happens();

        ScenarioModel currentScenarioModel = getScenario().getScenarioModel();
        assertThat( currentScenarioModel.getDescription() ).isEqualTo( title );
        assertThat( currentScenarioModel.getExplicitParameters() ).containsExactly( "milkInLiter", "ingredient", "caseNr" );

        ScenarioCaseModel scenarioCase = getScenario().getScenarioCaseModel();

        Word word = scenarioCase.getSteps().get( 0 ).getWords().get( 0 );
        assertThat( word.isIntroWord() ).isTrue();
        assertThat( word.getValue() ).isEqualTo( "Given" );

        word = scenarioCase.getSteps().get( 0 ).getWords().get( 1 );
        assertThat( word.isArg() ).isTrue();
        assertThat( word.getValue() ).isEqualTo( "" + milkInLiter );

        word = scenarioCase.getSteps().get( 2 ).getWords().get( 2 );
        assertThat( word.isArg() ).isTrue();
        assertThat( word.getValue() ).isEqualTo( "something" );

        StepModel stepModel = scenarioCase.getSteps().get( 3 );
        assertThat( stepModel.isFailed() ).isFalse();

        List<String> arguments = scenarioCase.getExplicitArguments();
        assertThat( arguments ).containsExactly( "" + milkInLiter, ingredient, "" + caseNr );
    }

}
