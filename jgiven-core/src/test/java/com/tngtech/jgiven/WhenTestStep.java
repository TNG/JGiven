package com.tngtech.jgiven;

import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class WhenTestStep extends Stage<WhenTestStep> {
    @ExpectedScenarioState
    int someIntValue;

    @ExpectedScenarioState
    int value1;

    @ExpectedScenarioState
    int value2;

    @ProvidedScenarioState
    int intResult;

    int afterStageCalled;

    public void both_values_are_multiplied_with_each_other() {
        intResult = value1 * value2;
    }

    public WhenTestStep something_happens() {
        return self();
    }

    public void an_exception_is_thrown() {
        throw new RuntimeException();
    }

    @AfterStage
    void someAfterStageMethod() {
        afterStageCalled++;
    }

}
