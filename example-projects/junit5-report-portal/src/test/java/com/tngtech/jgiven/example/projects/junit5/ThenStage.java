package com.tngtech.jgiven.example.projects.junit5;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;
import org.junit.jupiter.api.Assertions;

public class ThenStage extends Stage<ThenStage> {
    @ScenarioState(required = true)
    String result;

    public void the_result_is(String expectedResult) {
        Assertions.assertEquals(expectedResult, result);
    }
}
