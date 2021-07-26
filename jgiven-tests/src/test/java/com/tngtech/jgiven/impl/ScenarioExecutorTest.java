package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.JGivenTestConfiguration;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.base.StageName;
import com.tngtech.jgiven.impl.ScenarioExecutorTest.TestSteps;
import com.tngtech.jgiven.impl.intercept.StageNameInternal;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.tags.FeatureStepParameters;
import com.tngtech.jgiven.tags.Issue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(DataProviderRunner.class)
@JGivenConfiguration(JGivenTestConfiguration.class)
public class ScenarioExecutorTest extends SimpleScenarioTest<TestSteps> {
    @Mock
    Object mockedStageObject;

    @Before
    public void setup() {
        mockedStageObject = mock(new ByteBuddyStageClassCreator().createStageClass(Stage.class));
    }

    @DataProvider
    public static Object[][] primitiveArrays() {
        return new Object[][]{
                {"byte", new byte[]{1, 2, 3}},
                {"char", new char[]{'a', 'b', 'c'}},
                {"short", new short[]{1, 2, 3}},
                {"int", new int[]{1, 2, 3}},
                {"long", new long[]{1, 2, 3}},
                {"double", new double[]{1, 2, 3}},
                {"float", new float[]{1, 2, 3}},
                {"boolean", new boolean[]{true, false}},
        };
    }

    @Test
    public void methods_called_during_stage_construction_are_ignored_in_the_report() {
        given().some_stage_with_method_called_during_construction();
        then().the_method_does_not_appear_in_the_report(getScenario().getScenarioCaseModel());
    }

    @Test
    public void set_last_executed_stage_sets_to_the_given_object_if_not_null() {
        ScenarioExecutor scenarioExecutor = new ScenarioExecutor();
        StageName stageName = new StageName("GIVEN");

        scenarioExecutor.setLastExecutedStageNameWrapper(stageName);

        assertThat(scenarioExecutor.getLastExecutedStageNameWrapper()).isSameAs(stageName);
    }

    @Test
    public void set_last_executed_stage_does_not_set_to_the_given_object_if_null() {
        ScenarioExecutor scenarioExecutor = new ScenarioExecutor();
        StageName stageName = new StageName("GIVEN");
        scenarioExecutor.setLastExecutedStageNameWrapper(stageName);

        scenarioExecutor.setLastExecutedStageNameWrapper(null);

        assertThat(scenarioExecutor.getLastExecutedStageNameWrapper()).isSameAs(stageName);
    }

    @Test
    public void get_stage_name_wrapper_returns_a_new_stage_name_if_not_null() {
        ScenarioExecutor scenarioExecutor = new ScenarioExecutor();
        StageName stageName = new StageName("GIVEN");

        doReturn(stageName).when((StageNameInternal) mockedStageObject).__jgiven_getStageNameWrapper();

        assertThat(scenarioExecutor
                .getStageNameWrapper(mockedStageObject, null)).isSameAs(stageName);
    }

    @Test
    public void get_stage_name_wrapper_returns_the_last_executed_wrapper_if_given_wrapper_null() {
        ScenarioExecutor scenarioExecutor = new ScenarioExecutor();
        StageName stageName = new StageName("GIVEN");

        doReturn(null).when((StageNameInternal) mockedStageObject).__jgiven_getStageNameWrapper();

        assertThat(scenarioExecutor
                .getStageNameWrapper(mockedStageObject, stageName)).isSameAs(stageName);
    }

    @Test
    @UseDataProvider("primitiveArrays")
    @Issue("#1")
    @FeatureStepParameters
    public void step_methods_can_have_primitive_arrays_as_parameters(String type, Object array) {
        given().a_step_method_with_a_primitive_$_array_$_as_parameter(type, array);
        when().the_scenario_is_executed();
        then().no_exception_is_thrown();
    }

    public static class TestSteps {
        public void the_method_does_not_appear_in_the_report(@Hidden ScenarioCaseModel scenarioCaseModel) {
            StepModel stepModel = scenarioCaseModel.getFirstStep();
            assertThat(stepModel.getWords().get(1).getValue())
                    .isNotEqualTo("buildString")
                    .isEqualTo("some stage with method called during construction");
        }

        public void some_stage_with_method_called_during_construction() {
        }

        public void a_scenario_executor_object() {

        }

        public void a_step_method_with_a_primitive_$_array_$_as_parameter(String type, Object array) {
        }

        public void the_scenario_is_executed() {
        }

        public void no_exception_is_thrown() {
            assertThat(true).as("no exception is thrown").isTrue();
        }
    }
}
