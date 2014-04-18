package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.impl.ScenarioExecutorTest.TestSteps;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.report.model.StepModel;

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

    }
}
