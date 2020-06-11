package com.tngtech.jgiven.impl;

public class NavigableStageCreator {

    private final ScenarioExecutor scenarioExecutor;

    NavigableStageCreator( ScenarioExecutor scenarioExecutor ) {
        this.scenarioExecutor = scenarioExecutor;
    }

    @SuppressWarnings("unchecked")
    public <STAGE extends NavigableStage<ROOT, BACK, STAGE>, ROOT, BACK> STAGE nestedStage( STAGE prototype, ROOT root, BACK back ) {
        return (STAGE) scenarioExecutor.addNavigableStage( prototype.getClass() ).root( root ).back( back );
    }

}
