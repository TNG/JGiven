package com.tngtech.jgiven.junit.concurrency;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
final class Stages {
    private Stages() {
    }


    static class ParallelGivenStage extends Stage<ParallelGivenStage> {

        @ProvidedScenarioState
        private final ThreadLocal<String> scenarioState = new ThreadLocal<>();

        ParallelGivenStage a_thread_local_scenario_state() {
            logState(this, scenarioState);
            return this;
        }
    }

    static class ParallelWhenStage extends Stage<ParallelWhenStage> {

        @ExpectedScenarioState
        private ThreadLocal<String> scenarioState;

        ParallelWhenStage the_state_on_this_thread_is_set_to(String value) {
            logState(this, scenarioState);
            scenarioState.set(value);
            return this;
        }
    }

    static class ParallelThenStage extends Stage<ParallelThenStage> {
        @ExpectedScenarioState
        private ThreadLocal<String> scenarioState;

        ParallelThenStage the_value_on_this_thread_is(String expectation) {
            logState(this, scenarioState);
            assertThat(scenarioState.get()).isEqualTo(expectation);
            return this;
        }
    }

    private static void logState(Stage<?> stage, ThreadLocal<?> stageState) {
        LoggerFactory.getLogger(stage.getClass()).info("Object {} on thread {} with state {} and thread local value {}",
            stage, Thread.currentThread().getId(), stageState, stageState.get());
    }

}
