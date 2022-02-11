package com.tngtech.jgiven.testng.concurrency;

import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.testng.SimpleScenarioTest;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

@Test(singleThreaded = true)
@Ignore("root cause of error not yet fixed")
public class TestNgInjectedParallelizationTest extends SimpleScenarioTest<TestNgInjectedParallelizationTest> {

    @ScenarioStage
    private Stages.ParallelGivenStage parallelGivenStage;

    @ScenarioStage
    private Stages.ParallelWhenStage parallelWhenStage;

    @ScenarioStage
    private Stages.ParallelThenStage parallelThenStage;


    @Test(singleThreaded = false)
    @Pending
    void firstTest() {
        int i = 0;//for (int i = 1; i <= 100; i++) {
            parallelGivenStage.a_thread_local_scenario_state();
            parallelWhenStage.the_state_on_this_thread_is_set_to("I am the greatest" + i);
            parallelThenStage.the_value_on_this_thread_is("I am the greatest" + i);
        //}

    }

    @Test(singleThreaded = false)
    @Pending
    void secondTest() {
        int i = 0;//for (int i = 1; i <= 100; i++) {
            parallelGivenStage.a_thread_local_scenario_state();
            parallelWhenStage.the_state_on_this_thread_is_set_to("I am the best" + i);
            parallelThenStage.the_value_on_this_thread_is("I am the best" + i);
        //}
    }
}
