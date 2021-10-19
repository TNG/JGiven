package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class ProxyClassPerformanceTest {

    @Test
    public void test_creation_of_proxy_classes() {
        Set<Long> megabytesOfMemoryUsedInCycle = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            ScenarioBase scenario = new ScenarioBase();
            TestStage testStage = scenario.addStage(TestStage.class);
            testStage.something();
            if (i % 100 == 0) {
                System.gc();
                long usedMemory = calculateMemoryUsageRoundedDownToMegabytes();
                System.out.println("Used memory: " + usedMemory);
                megabytesOfMemoryUsedInCycle.add(usedMemory);
            }
        }

        assertThat(megabytesOfMemoryUsedInCycle)
            .describedAs("Set should only contain 1 item, "
                    + "but the  first iteration might use more memory, "
                    + "so the set might contain 2 items")
            .hasSizeBetween(1, 2);
    }

    private long calculateMemoryUsageRoundedDownToMegabytes() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
    }

    public static class TestStage {
        public void something() {
        }
    }
}
