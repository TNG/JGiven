package com.tngtech.jgiven;

import com.tngtech.jgiven.Scenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class WhenScenario extends ScenarioStages<WhenScenario> {

    @ProvidedScenarioState
    Scenario<?, ?, ?> scenario;

    public void the_scenario_is_created() {
        scenario = Scenario.create( givenStage, whenStage, thenStage );
    }

}
