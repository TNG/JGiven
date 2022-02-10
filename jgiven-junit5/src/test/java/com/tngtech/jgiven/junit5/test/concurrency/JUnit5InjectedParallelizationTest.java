package com.tngtech.jgiven.junit5.test.concurrency;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit5.JGivenExtension;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(JGivenExtension.class)
class JUnit5InjectedParallelizationTest {

    @ScenarioStage
    private Stages.ParallelGivenStage parallelGivenStage;

    @ScenarioStage
    private Stages.ParallelWhenStage parallelWhenStage;

    @ScenarioStage
    private Stages.ParallelThenStage parallelThenStage;


    static IntStream iterationProvider() {
        return IntStream.range(0, 100);
    }

    @ParameterizedTest
    @MethodSource("iterationProvider")
    void firstTest(int i) {
        parallelGivenStage.given().a_thread_local_scenario_state();
        parallelWhenStage.when().the_state_on_this_thread_is_set_to("I am the greatest" + i);
        parallelThenStage.then().the_value_on_this_thread_is("I am the greatest" + i);
    }

    @Test
    void secondTest() {
        for (int i = 1; i <= 100; i++) {
            parallelGivenStage.given().a_thread_local_scenario_state();
            parallelWhenStage.when().the_state_on_this_thread_is_set_to("I am the best" + i);
            parallelThenStage.then().the_value_on_this_thread_is("I am the best" + i);
        }
    }
}
