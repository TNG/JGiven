package com.tngtech.jgiven;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;

public class ScenarioStages<SELF extends ScenarioStages<?>> extends Stage<SELF> {
    @ScenarioState
    Class<?> givenStage;

    @ScenarioState
    Class<?> whenStage;

    @ScenarioState
    Class<?> thenStage;
}
