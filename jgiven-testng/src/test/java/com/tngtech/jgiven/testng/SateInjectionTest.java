package com.tngtech.jgiven.testng;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;

/**
 * Example of how inject state from test classes into stages
 */
@Listeners(ScenarioTestListener.class)
public class SateInjectionTest {

    @ProvidedScenarioState
    int value = 21;

    @ScenarioStage
    TestSteps testSteps;

    @Test
    public void state_can_be_injected_from_the_test_class() {
        testSteps
            .then().value_was_injected();
    }

    static class TestSteps extends Stage<TestSteps> {

        @ExpectedScenarioState
        int value;

        public void value_was_injected() {
            assertThat(value).isEqualTo(21);

        }
    }

}
