package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.impl.ScenarioExecutorTest.TestSteps;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.tags.FeatureStepParameters;
import com.tngtech.jgiven.tags.Issue;

@RunWith( DataProviderRunner.class )
public class ScenarioExecutorTest extends SimpleScenarioTest<TestSteps> {

    @Test
    public void methods_called_during_stage_construction_are_ignored_in_the_report() {
        given().some_stage_with_method_called_during_construction();
        then().the_method_does_not_appear_in_the_report();
    }

    public static class TestSteps {
        String test = buildString();

        public String buildString() {
            return "testString";
        }

        public void the_method_does_not_appear_in_the_report() {
            StepModel stepModel = ScenarioExecutorTest.writerRule.getTestCaseModel().getFirstStepModelOfLastScenario();
            assertThat( stepModel.words.get( 1 ).value )
                .isNotEqualTo( "buildString" )
                .isEqualTo( "some stage with method called during construction" );
        }

        public void some_stage_with_method_called_during_construction() {}

        public void a_step_method_with_a_primitive_$_array_$_as_parameter( String type, Object array ) {}

        public void the_scenario_is_executed() {}

        public void no_exeception_is_thrown() {
            assertThat( true ).as( "no exception is thrown" ).isTrue();
        }

    }

    @DataProvider
    public static Object[][] primitiveArrays() {
        return new Object[][] {
            { new byte[] { 1, 2, 3 } },
            { new char[] { 'a', 'b', 'c' } },
            { new short[] { 1, 2, 3 } },
            { new int[] { 1, 2, 3 } },
            { new long[] { 1, 2, 3 } },
            { new double[] { 1, 2, 3 } },
            { new float[] { 1, 2, 3 } },
            { new boolean[] { true, false } },
        };
    }

    @Test
    @UseDataProvider( "primitiveArrays" )
    @Issue( "#1" )
    @FeatureStepParameters
    public void step_methods_can_have_primitive_arrays_as_parameters( Object array ) {
        given().a_step_method_with_a_primitive_$_array_$_as_parameter( array.getClass().getComponentType().getSimpleName(), array );
        when().the_scenario_is_executed();
        then().no_exeception_is_thrown();
    }
}
