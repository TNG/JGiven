package com.tngtech.jgiven.example.projects.testng;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;

class GivenStage extends Stage<GivenStage> {

    @ScenarioState
    String message;

    GivenStage message(String message) {
        this.message = message;
        return self();
    }
}
