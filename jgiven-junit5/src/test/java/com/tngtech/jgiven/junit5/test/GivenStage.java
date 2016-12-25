package com.tngtech.jgiven.junit5.test;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class GivenStage {

    @ProvidedScenarioState
    String someState;

    public void some_state() {
        someState = "Some State";
    }

}
