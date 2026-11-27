package com.tngtech.jgiven.junit6.test;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
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
