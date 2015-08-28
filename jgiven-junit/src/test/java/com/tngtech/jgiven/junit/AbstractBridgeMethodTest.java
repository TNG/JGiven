package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.junit.ScenarioExecutionTest.TestStage;

public abstract class AbstractBridgeMethodTest<T extends AbstractBridgeMethodTest.BridgeMethodTestStage> extends
        ScenarioTest<T, TestStage, TestStage> {

    @Test
    public void bridge_methods_are_correctly_handled() {
        given().method_that_is_overidden_with_different_return_type();

        assertThat( getScenario().getScenarioCaseModel().getSteps() ).isNotEmpty();
    }

    static class BridgeMethodTestStage {
        BridgeMethodTestStage method_that_is_overidden_with_different_return_type() {
            return this;
        }
    }

}
