package com.tngtech.jgiven.junit5.test;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.junit.jupiter.api.Assertions;

public class ThenStage {
    @ExpectedScenarioState
    String someState;

    @ExpectedScenarioState
    String someResult;

    void some_outcome() {
        Assertions.assertNotNull(someState);
        Assertions.assertNotNull(someResult);
    }
}
