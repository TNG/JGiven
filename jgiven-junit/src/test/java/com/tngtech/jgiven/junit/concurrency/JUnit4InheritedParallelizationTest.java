package com.tngtech.jgiven.junit.concurrency;

import com.googlecode.junittoolbox.ParallelParameterized;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(ParallelParameterized.class)
public class JUnit4InheritedParallelizationTest
    extends ScenarioTest<Stages.ParallelGivenStage, Stages.ParallelWhenStage, Stages.ParallelThenStage> {

    int i;

    @Parameters
    public static Iterable<Object[]>iterationProvider() {
        return IntStream.range(0, 100)
                .mapToObj(value -> new Object[]{value})
                .collect(Collectors.toList());
    }

    public JUnit4InheritedParallelizationTest(int i){
        this.i = i;
    }


    @Test
    public void testParallelExecution() {
        given().a_thread_local_scenario_state();
        when().the_state_on_this_thread_is_set_to("I am the greatest " + i);
        then().the_value_on_this_thread_is("I am the greatest " + i);
    }
}
