package com.tngtech.jgiven.exampleprojects.java.module;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;

/**
 * Public stage that consumes the {@code @ScenarioState} produced by
 * {@link PackagePrivateStage}.
 */
public class ThenStage extends Stage<ThenStage> {

    @ScenarioState(required = true)
    String result;

    public void the_result_is(String expected) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, result);
    }
}
