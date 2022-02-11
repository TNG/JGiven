package com.tngtech.jgiven.testng.concurrency;

import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.testng.ScenarioTest;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

@Test(singleThreaded = false )
@Ignore("root cause of error not yet fixed")
public class TestNgInheritedParallelizationTest
    extends ScenarioTest<Stages.ParallelGivenStage, Stages.ParallelWhenStage, Stages.ParallelThenStage> {

    @Test
    @Pending
    public void fistTest() {
        int i =0; //for (int i = 1; i <= 100; i++) {
            given().a_thread_local_scenario_state();
            when().the_state_on_this_thread_is_set_to("I am the greatest" + i);
            then().the_value_on_this_thread_is("I am the greatest" + i);
        //}

    }

    @Test
    @Pending
    void secondTest() {
        int i = 0; //for (int i = 1; i <= 100; i++) {
            given().a_thread_local_scenario_state();
            when().the_state_on_this_thread_is_set_to("I am the best" + i);
            then().the_value_on_this_thread_is("I am the best" + i);
        //}
    }


}
