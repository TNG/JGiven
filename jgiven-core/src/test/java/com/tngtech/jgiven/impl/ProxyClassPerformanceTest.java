package com.tngtech.jgiven.impl;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.base.ScenarioTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class ProxyClassPerformanceTest {

    @Test
    public void test_creation_of_proxy_classes() {
        for (int i = 0; i < 1000; i++) {
            ScenarioBase scenario = new ScenarioBase();
            TestStage testStage = scenario.addStage(TestStage.class);
            testStage.something();
            if (i % 100 == 0) {
                System.gc();
                System.out.println("Used memory: "+      (  Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
            }
        }
    }

    public static class TestStage {
        public void something() {}
    }

}
