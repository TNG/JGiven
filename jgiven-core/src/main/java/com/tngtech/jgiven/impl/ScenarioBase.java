package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.integration.CanWire;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelBuilder;

public class ScenarioBase {
    protected final ScenarioExecutor executor = new ScenarioExecutor();
    protected final ReportModelBuilder modelBuilder = new ReportModelBuilder();

    public ScenarioBase() {
        executor.setListener( modelBuilder );
    }

    public void setModel( ReportModel scenarioCollectionModel ) {
        modelBuilder.setModel( scenarioCollectionModel );
    }

    public ReportModel getModel() {
        return modelBuilder.getScenarioCollectionModel();
    }

    public <T> T addStage( Class<T> stepsClass ) {
        return executor.addStage( stepsClass );
    }

    public void finished() {
        executor.finished();
    }

    public ScenarioExecutor getExecutor() {
        return executor;
    }

    public void wireSteps( CanWire canWire ) {
        executor.wireSteps( canWire );
    }

    public ReportModelBuilder getModelBuilder() {
        return modelBuilder;
    }

    public ScenarioBase startScenario( String description ) {
        executor.startScenario( description );
        return this;
    }

}
