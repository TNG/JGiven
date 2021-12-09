package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

/**
 * test that the continued creation of proxy classes does not
 * constantly consume memory.
 */
public class ProxyClassPerformanceTest {

    private static final int NUMBER_OF_RUNS = 10000;
    private static final int PROBE_INTERVAL = 100;
    private static final double GROWTH_EXPECTED_IF_LAST_REPORTED_GROWTH_IS_POSITIVE = 0.11;


    @Test
    public void test_creation_of_proxy_classes() {
        List<Long> memoryUsageRecordInMebibytes = new ArrayList<>(NUMBER_OF_RUNS / PROBE_INTERVAL);
        for (int i = 0; i < NUMBER_OF_RUNS; i++) {
            ScenarioBase scenario = new ScenarioBase();
            TestStage testStage = scenario.addStage(TestStage.class);
            testStage.something();
            if (i % PROBE_INTERVAL == 0) {
                System.gc();
                long usedMemory = calculateMemoryUsageRoundedDownToMebibytes();
                System.out.println("Used memory: " + usedMemory);
                memoryUsageRecordInMebibytes.add(usedMemory);
            }
        }
        List<Long> growth = calculateChangeInMemoryConsumption(memoryUsageRecordInMebibytes);
        double averageConsumptionChange = average(growth);
        assertThat(averageConsumptionChange)
            .as("There is no net increase of memory consumption "
            + "for the continued creation and discarding of proxy classes.")
            .isLessThanOrEqualTo(GROWTH_EXPECTED_IF_LAST_REPORTED_GROWTH_IS_POSITIVE);
    }

    private List<Long> calculateChangeInMemoryConsumption(List<Long> record) {
        List<Long> growth = new ArrayList<>(record.size() - 1);
        for (int i = 1; i < record.size(); i++) {
            growth.add(record.get(i) - record.get(i - 1));
        }
        return growth;
    }

    private double average(List<Long> data) {
        return data.stream()
            .mapToDouble(Double::valueOf)
            .reduce((currentAverage, dataPoint) -> currentAverage + dataPoint / data.size())
            .orElse(0.0);
    }

    private long calculateMemoryUsageRoundedDownToMebibytes() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
    }

    static class TestStage {
        public void something() {
        }
    }
}
