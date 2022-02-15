package com.tngtech.jgiven.tests;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestWithPerClassLifecycle extends ScenarioTestForTesting<GivenTestStage, WhenTestStage, ThenTestStage> {

    @Test
    void innocuous_jgiven_test() {
        given().nothing();
        when().something_happens();
        then().something_happened();
    }
}
