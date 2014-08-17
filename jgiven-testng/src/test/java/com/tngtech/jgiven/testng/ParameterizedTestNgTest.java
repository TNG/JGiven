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
        given().$_l_milk( milkInLiter )
            .and().ingredient( ingredient );
        when().mixed_with( "something" );
        then().nothing_happens();

        ScenarioModel currentScenarioModel = getScenario().getModel().getLastScenarioModel();
        assertThat( currentScenarioModel.description ).isEqualTo( "parameters are handled correctly" );
        assertThat( currentScenarioModel.parameterNames ).containsExactly( "milkInLiter", "ingredient", "caseNr" );

        ScenarioCaseModel scenarioCase = currentScenarioModel.getCase( caseNr );

        Word word = scenarioCase.steps.get( 0 ).words.get( 0 );
        assertThat( word.isIntroWord ).isTrue();
        assertThat( word.value ).isEqualTo( "Given" );

        word = scenarioCase.steps.get( 0 ).words.get( 1 );
        assertThat( word.isArg() ).isTrue();
        assertThat( word.value ).isEqualTo( "" + milkInLiter );

        word = scenarioCase.steps.get( 2 ).words.get( 2 );
        assertThat( word.isArg() ).isTrue();
        assertThat( word.value ).isEqualTo( "something" );

        StepModel stepModel = scenarioCase.steps.get( 3 );
        assertThat( stepModel.isFailed() ).isFalse();

        List<String> arguments = scenarioCase.arguments;
        assertThat( arguments ).containsExactly( "" + milkInLiter, ingredient, "" + caseNr );

    }
}
