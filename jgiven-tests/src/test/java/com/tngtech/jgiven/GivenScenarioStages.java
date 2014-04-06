package com.tngtech.jgiven;

import com.tngtech.jgiven.annotation.BeforeStage;

public class GivenScenarioStages<SELF extends GivenScenarioStages<?>> extends ScenarioStages<SELF> {

    @BeforeStage
    public void init() {
        givenStage = Object.class;
        whenStage = Object.class;
        thenStage = Object.class;
    }

    public void a_given_stage_of_type( Class<?> clazz ) {
        givenStage = clazz;
    }
}
