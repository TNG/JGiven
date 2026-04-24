package com.tngtech.jgiven.junit6.test;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class GivenStage {

    @ProvidedScenarioState
    String someState;

    public void some_state() {
        someState = "Some State";
    }

}
