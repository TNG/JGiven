package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.impl.ScenarioBase;

public class StandaloneScenarioRule extends ScenarioExecutionRule {

    public StandaloneScenarioRule() {
        super( new ScenarioBase() );
    }

    public <T> T addStage( Class<T> stepsClass ) {
        return scenario.addStage( stepsClass );
    }

}
