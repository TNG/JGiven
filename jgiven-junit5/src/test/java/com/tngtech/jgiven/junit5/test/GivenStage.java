package com.tngtech.jgiven.junit5.test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class GivenStage extends Stage<GivenStage> {

    @ProvidedScenarioState
    String someState;

    public GivenStage some_state() {
        someState = "Some State";
        return self();
    }

    public GivenStage some_action() {
        return self();
    }

    public GivenStage some_outcome() {
        return self();
    }

}
