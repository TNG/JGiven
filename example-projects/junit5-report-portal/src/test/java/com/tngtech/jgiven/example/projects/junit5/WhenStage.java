package com.tngtech.jgiven.example.projects.junit5;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;

public class WhenStage extends Stage<WhenStage> {
    @ScenarioState(required = true)
    String message;

    @ScenarioState
    String result;

    public void handle_message() {
        result = message + " 5!";
    }

    public void failure() {
        throw new RuntimeException("I am a failure");
    }
}
