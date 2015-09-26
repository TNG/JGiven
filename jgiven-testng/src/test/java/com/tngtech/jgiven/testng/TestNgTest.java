package com.tngtech.jgiven.testng;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.testng.annotations.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.testng.TestNgTest.TestSteps;

public class TestNgTest extends ScenarioTest<TestSteps, TestSteps, TestSteps> {

    @Test
    public void Milk_and_Sugar_are_mixed_to_Sugar_Milk() throws Throwable {
        given().milk()
            .and().sugar();
        when().mixed();
        then().you_get_sugar_milk();

        getScenario().finished();

        ScenarioModel scenarioModel = getScenario().getScenarioModel();
        assertThat( scenarioModel.getDescription() ).isEqualTo( "Milk and Sugar are mixed to Sugar Milk" );
        assertThat( scenarioModel.getTestMethodName() ).isEqualTo( "Milk_and_Sugar_are_mixed_to_Sugar_Milk" );
        assertThat( scenarioModel.getClassName() ).isEqualTo( getClass().getName() );
        assertThat( scenarioModel.getExplicitParameters() ).isEmpty();
        assertThat( scenarioModel.getScenarioCases() ).hasSize( 1 );

        ScenarioCaseModel scenarioCaseModel = scenarioModel.getCase( 0 );
        assertThat( scenarioCaseModel.getExplicitArguments() ).isEmpty();
        assertThat( scenarioCaseModel.getCaseNr() ).isEqualTo( 1 );
        assertThat( scenarioCaseModel.getErrorMessage() ).isNull();
        assertThat( scenarioCaseModel.isSuccess() ).isTrue();

        List<StepModel> steps = scenarioCaseModel.getSteps();
        assertThat( steps ).hasSize( 4 );
        assertThat( steps.get( 0 ).getCompleteSentence() ).isEqualTo( "Given milk" );
        assertThat( steps.get( 1 ).getCompleteSentence() ).isEqualTo( "and sugar" );
        assertThat( steps.get( 2 ).getCompleteSentence() ).isEqualTo( "When mixed" );
        assertThat( steps.get( 3 ).getCompleteSentence() ).isEqualTo( "Then you get sugar milk" );
    }

    public static class TestSteps extends Stage<TestSteps> {

        @ScenarioState
        private int milkInLiter;

        @ScenarioState
        private int sugarInGramms;

        @ScenarioState
        private String result = "";

        @ScenarioState
        private String someIngredient;

        public TestSteps milk() {
            return $_l_milk( 1 );
        }

        public TestSteps $_l_milk( int milkInLiter ) {
            this.milkInLiter = milkInLiter;
            return self();
        }

        public void nothing_happens() {}

        public void nothing() {}

        public void work_is_in_progress() {}

        public TestSteps starting() {
            return this;
        }

        public TestSteps work() {
            return this;
        }

        public TestSteps something_fails() {
            throw new IllegalStateException( "Something failed" );
        }

        public TestSteps you_get_sugar_milk() {
            assertThat( result ).isEqualTo( "SugarMilk" );
            return this;
        }

        public TestSteps mixed() {
            if( sugarInGramms > 0 ) {
                result += "Sugar";
            }

            if( milkInLiter > 0 ) {
                result += "Milk";
            }
            return self();
        }

        public TestSteps sugar() {
            sugarInGramms = 100;
            return self();
        }

        public void ingredient( String someIngredient ) {
            this.someIngredient = someIngredient;
        }

        public void mixed_with( String something ) {}

        public void something() {}

    }
}
