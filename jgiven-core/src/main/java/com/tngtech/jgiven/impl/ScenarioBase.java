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
        modelBuilder.setReportModel( scenarioCollectionModel );
    }

    public ReportModel getModel() {
        return modelBuilder.getReportModel();
    }

    public <T> T addStage( Class<T> stepsClass ) {
        return executor.addStage( stepsClass );
    }

    /**
     * Finishes the scenario.
     * 
     * @throws Throwable in case some exception has been thrown during the execution of the scenario
     */
    public void finished() throws Throwable {
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
