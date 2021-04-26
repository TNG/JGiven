package com.tngtech.jgiven.example.projects.testng;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;

class WhenStage extends Stage<WhenStage> {
    @ScenarioState(required = true)
    String message;

    @ScenarioState
    String result;

    public void handle_message() {
        result = message + " TestNG!";
    }
}
