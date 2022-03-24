package com.tngtech.jgiven.junit.concurrency;

import com.googlecode.junittoolbox.ParallelParameterized;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(ParallelParameterized.class)
public class JUnit4InjectedParallelizationTest extends SimpleScenarioTest<JUnit4InjectedParallelizationTest.VoidStage> {

    int i;

    @Parameters
    public static Iterable<Object[]>iterationProvider() {
        return IntStream.range(0, 100)
                .mapToObj(value -> new Object[]{value})
                .collect(Collectors.toList());
    }

    public JUnit4InjectedParallelizationTest(int i){
        this.i = i;
    }

    @ScenarioStage
    private Stages.ParallelGivenStage parallelGivenStage;

    @ScenarioStage
    private Stages.ParallelWhenStage parallelWhenStage;

    @ScenarioStage
    private Stages.ParallelThenStage parallelThenStage;



    @Test
    public void testParallelExecution() {
        parallelGivenStage.given().a_thread_local_scenario_state();
        parallelWhenStage.when().the_state_on_this_thread_is_set_to("I am the best " + i);
        parallelThenStage.then().the_value_on_this_thread_is("I am the best " + i);
    }

    static class VoidStage extends Stage<VoidStage>{}
}

