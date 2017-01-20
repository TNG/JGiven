package com.tngtech.jgiven.junit5.test;

import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.junit.jupiter.api.Assertions;

public class WhenStage {

    @ExpectedScenarioState
    String someState;

    @ProvidedScenarioState
    String someResult;

    @BeforeScenario
    protected void someBeforeScenario() {
        System.out.println("BEFORE SCENARIO");
    }

    @AfterScenario
    protected void someAfterScenario() {
        System.out.println("AFTER SCENARIO");
    }


    @BeforeStage
    protected void someBeforeStage() {
        Assertions.assertNotNull(someState);
    }

    void some_action() {
        Assertions.assertNotNull(someState);
        someResult = "Some Result";
    }

    public void some_failing_step() {
        Assertions.assertTrue(false, "Intentionally failing");
    }
}
