package com.tngtech.jgiven.impl;

import java.lang.reflect.Method;
import java.util.List;

import com.tngtech.jgiven.impl.util.AssertionUtil;
import com.tngtech.jgiven.integration.CanWire;
import com.tngtech.jgiven.report.model.NamedArgument;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;

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
    protected final ScenarioModelBuilder modelBuilder = new ScenarioModelBuilder();
    private boolean initialized = false;

    public ScenarioBase() {}

    public void setModel( ReportModel reportModel ) {
        assertNotInitialized();
        modelBuilder.setReportModel( reportModel );
    }

    public ScenarioModel getScenarioModel() {
        return modelBuilder.getScenarioModel();
    }

    public ScenarioCaseModel getScenarioCaseModel() {
        return modelBuilder.getScenarioCaseModel();
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

    public void setExecutor( ScenarioExecutor executor ) {
        assertNotInitialized();
        this.executor = executor;
    }

    public void wireSteps( CanWire canWire ) {
        executor.wireSteps( canWire );
    }

    public ScenarioBase startScenario( Class<?> testClass, Method method, List<NamedArgument> arguments ) {
        performInitialization();
        executor.startScenario( testClass, method, arguments );
        return this;
    }

    public ScenarioBase startScenario( String description ) {
        performInitialization();
        executor.startScenario( description );
        return this;
    }

    private void performInitialization() {
        if( modelBuilder == null ) {
            throw new IllegalStateException( "modelBuilder must be set before Scenario can be initalized." );
        }
        if( !initialized ) {
            executor.setListener( modelBuilder );
            initialize();
            initialized = true;
        }
    }

    protected void initialize() {
        // extension point for two phase initialization
    }

    protected void assertNotInitialized() {
        AssertionUtil.assertTrue( !initialized, "Scenario is already initialized" );
    }

    /**
     * Adds a new section to the scenario
     * <h1>EXPERIMENTAL FEATURE</h1>
     * This is an experimental feature. It might change in the future.
     * If you have any feedback regarding this feature, please let us know
     * by creating an issue at https://github.com/TNG/JGiven/issues
     * @param sectionTitle the title of the section
     * @since 0.11.0
     */
    public void section( String sectionTitle ) {
        executor.addSection( sectionTitle );
    }

}
