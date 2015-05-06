package com.tngtech.jgiven.testng;

import org.testng.annotations.Test;

public class SimpleTestNgScenarioTest extends SimpleScenarioTest<TestNgTest.TestSteps> {

    @Test
    public void simple_scenario_test_works_for_TestNG() {
        given().something();

    }
}
