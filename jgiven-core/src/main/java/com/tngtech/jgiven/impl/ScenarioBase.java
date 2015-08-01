package com.tngtech.jgiven.impl;

import java.lang.reflect.Method;
import java.util.List;

import com.tngtech.jgiven.impl.util.AssertionUtil;
import com.tngtech.jgiven.integration.CanWire;
import com.tngtech.jgiven.report.model.NamedArgument;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelBuilder;

/**
 * Base class for a Scenario.
 * <p>
 * Before a Scenario can be used it must be properly configured. After the configuration phase
 * {@link #startScenario} must be called in order to execute the scenario. Once started a scenario
 * cannot be reconfigured.
 * <p>
 * {@link #initialize} should be overridden by subclasses to apply their own configuration to the scenario.
 *
 */
public class ScenarioBase {
    protected ScenarioExecutor executor = new StandaloneScenarioExecutor();
    protected final ReportModelBuilder modelBuilder = new ReportModelBuilder();
    private boolean initialized = false;

    public ScenarioBase() {
    }

    public void setModel( ReportModel scenarioCollectionModel ) {
        assertNotInitialized();
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

    public void setExecutor(ScenarioExecutor executor) {
        assertNotInitialized();
        this.executor = executor;
    }

    public void wireSteps( CanWire canWire ) {
        executor.wireSteps( canWire );
    }

    public ReportModelBuilder getModelBuilder() {
        return modelBuilder;
    }

    public ScenarioBase startScenario(Method method, List<NamedArgument> arguments) {
        performInitialization();
        executor.startScenario(method, arguments);
        return this;
    }

    public ScenarioBase startScenario( String description ) {
        performInitialization();
        executor.startScenario( description );
        return this;
    }

    private void performInitialization() {
        if (modelBuilder == null) {
            throw new IllegalStateException("modelBuilder must be set before Scenario can be initalized.");
        }
        if (!initialized) {
            executor.setListener( modelBuilder );
            initialize();
            initialized = true;
        }
    }

    protected void initialize() {
        // extension point for two phase initialization
    }

    protected void assertNotInitialized() {
        AssertionUtil.assertTrue(!initialized, "Scenario is already initialized");
    }

}
