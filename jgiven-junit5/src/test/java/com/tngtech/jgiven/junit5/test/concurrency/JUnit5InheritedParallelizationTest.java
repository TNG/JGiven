package com.tngtech.jgiven.junit5.test.concurrency;

import com.tngtech.jgiven.junit5.ScenarioTest;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Execution(ExecutionMode.CONCURRENT)
class JUnit5InheritedParallelizationTest
    extends ScenarioTest<Stages.ParallelGivenStage, Stages.ParallelWhenStage, Stages.ParallelThenStage> {

    public static IntStream iterationProvider() {
        return IntStream.range(0, 100);
    }

    @ParameterizedTest
    @MethodSource("iterationProvider")
    void firstTest(int i) {
        given().a_thread_local_scenario_state();
        when().the_state_on_this_thread_is_set_to("I am the greatest" + i);
        then().the_value_on_this_thread_is("I am the greatest" + i);
    }

    @Test
    void secondTest() {
        for (int i = 1; i <= 100; i++) {
            given().a_thread_local_scenario_state();
            when().the_state_on_this_thread_is_set_to("I am the best" + i);
            then().the_value_on_this_thread_is("I am the best" + i);
        }
    }
}
