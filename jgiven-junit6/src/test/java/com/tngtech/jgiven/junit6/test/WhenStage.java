package com.tngtech.jgiven.junit5.test;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.junit.jupiter.api.Assertions;

public class WhenStage {

    @ExpectedScenarioState
    String someState;

    @ProvidedScenarioState
    String someResult;

    void some_action() {
        Assertions.assertNotNull(someState);
        someResult = "Some Result";
    }

    void some_action_with_a_parameter(String s) {
        someResult = "s";
    }

    public WhenStage some_failing_step() {
        Assertions.assertTrue(false, "Intentionally failing");
        return this;
    }
}
