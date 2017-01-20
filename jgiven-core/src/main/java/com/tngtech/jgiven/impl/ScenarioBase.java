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
    protected ScenarioExecutor executor = new ScenarioExecutor();
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

    public void setStageCreator(StageCreator stageCreator) {
        this.executor.setStageCreator(stageCreator);
    }

    public void addIntroWord( String word ) {
        executor.addIntroWord( word );
    }

    /**
     * Alias for {@link #addStage}
     */
    public <T> T stage(Class<T> stageClass) {
        return addStage(stageClass);
    }

    /**
     * Alias for {@link #addIntroWord(String)}
     * @see #addIntroWord(String)
     */
    public ScenarioBase intro(String introWord) {
        addIntroWord(introWord);
        return this;
    }

    /**
     * Convenience method for adding the 'given' intro word
     * and adding a stage class. Equivalent to
     *
     * <pre>
     *     addIntroWord("given");
     *     return addStage(stageClass);
     * </pre>
     *
     * @see #addIntroWord(String)
     * @see #addStage(Class)
     */
    public <T> T given(Class<T> stageClass) {
        addIntroWord("given");
        return addStage(stageClass);
    }

    /**
     * Convenience method for adding the 'when' intro word
     * and adding a stage class. Equivalent to
     *
     * <pre>
     *     addIntroWord("when");
     *     return addStage(stageClass);
     * </pre>
     *
     * @see #addIntroWord(String)
     * @see #addStage(Class)
     */
    public <T> T when(Class<T> stageClass) {
        addIntroWord("when");
        return addStage(stageClass);
    }

    /**
     * Convenience method for adding the 'then' intro word
     * and adding a stage class. Equivalent to
     *
     * <pre>
     *     addIntroWord("then");
     *     return addStage(stageClass);
     * </pre>
     *
     * @see #addIntroWord(String)
     * @see #addStage(Class)
     */
    public <T> T then(Class<T> stageClass) {
        addIntroWord("then");
        return addStage(stageClass);
    }

}
