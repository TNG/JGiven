package com.tngtech.jgiven.example.projects.testng;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import org.testng.asserts.Assertion;

class ThenStage extends Stage<ThenStage> {
    @ScenarioState(required = true)
    String result;

    public void the_result_is(String expectedResult) {
        new Assertion().assertEquals(expectedResult, result);
    }
}
