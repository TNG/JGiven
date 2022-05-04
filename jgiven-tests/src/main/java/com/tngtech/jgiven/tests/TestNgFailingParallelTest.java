package com.tngtech.jgiven.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.testng.SimpleScenarioTest;
import org.testng.annotations.Test;

@Test(singleThreaded = false)
public class TestNgFailingParallelTest extends SimpleScenarioTest<TestNgFailingParallelTest> {

    @ScenarioStage
    private ParallelGivenWhenStage parallelGivenWhenStage;

    @ScenarioStage
    private ParallelThenStage parallelThenStage;


    @Test
    void firstTest() {
        for (int i = 0; i < 100; i++) {
            parallelGivenWhenStage.a_thread_local_scenario_state();
            parallelGivenWhenStage.the_state_on_this_thread_is_set_to("I am the greatest" + i);
            parallelThenStage.the_value_on_this_thread_is("I am the greatest" + i);
        }
    }

    @Test
    void secondTest() {
        for (int i = 0; i < 100; i++) {
            parallelGivenWhenStage.a_thread_local_scenario_state();
            parallelGivenWhenStage.the_state_on_this_thread_is_set_to("I am the best" + i);
            parallelThenStage.the_value_on_this_thread_is("I am the best" + i);
        }
    }

    static class ParallelGivenWhenStage extends Stage<ParallelGivenWhenStage> {

        @ExpectedScenarioState
        private String scenarioState;

        ParallelGivenWhenStage a_thread_local_scenario_state() {
            return this;
        }

        ParallelGivenWhenStage the_state_on_this_thread_is_set_to(String value) {
            scenarioState = value;
            return this;
        }
    }

    static class ParallelThenStage extends Stage<ParallelThenStage> {

        @ExpectedScenarioState
        private String scenarioState;

        ParallelThenStage the_value_on_this_thread_is(String expectation) {
            assertThat(scenarioState).isEqualTo(expectation);
            return this;
        }
    }
}
