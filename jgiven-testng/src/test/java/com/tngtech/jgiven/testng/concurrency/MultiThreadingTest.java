package com.tngtech.jgiven.testng.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.testng.ScenarioTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


@Test(singleThreaded=false)
public class MultiThreadingTest extends ScenarioTest<MultiThreadingTest.GivenSteps,
        MultiThreadingTest.WhenSteps, MultiThreadingTest.WhenSteps> {

    @Test(dataProvider = "data")
    public void multi_threading_works(int i) {
        GivenSteps given = given();
        assertThat(given).isNotNull();
        given.thread(Thread.currentThread().getId());
        then().value_is_injected();

    }

    @DataProvider(parallel = true)
    public Object[][] data() {
        int n = 100;
        Object[][] data = new Object[n][];
        for (int i = 0; i < n; i++) {
            data[i] = new Object[] { i };
        }
        return data;
    }

    public static class GivenSteps {
        @ScenarioState
        private String test;

        void thread(long i) {
            test = "thread "+i;
        }
    }

    public static class WhenSteps {
        @ScenarioState
        private String test;

        void value_is_injected() {
            assertThat(test).isNotNull();
        }
    }

}
