package com.tngtech.jgiven.example.projects.junit5;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioState;

public class GivenStage extends Stage<GivenStage> {

    @ScenarioState
    String message;

    GivenStage message(@Quoted String message) {
        this.message = message;
        return self();
    }
}
