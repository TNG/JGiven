package com.tngtech.jgiven.report.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ScenarioModel {
    private String className;
    private String testMethodName;
    private String description;
    private Set<Tag> tags = Sets.newLinkedHashSet();
    private boolean notImplementedYet;
    private List<String> explicitParameters = Lists.newArrayList();
    private List<String> derivedParameters = Lists.newArrayList();
    private boolean casesAsTable;
    private final List<ScenarioCaseModel> scenarioCases = Lists.newArrayList();
    private long durationInNanos;
    private ExecutionStatus executionStatus;

    public void accept( ReportModelVisitor visitor ) {
        visitor.visit( this );
        for( ScenarioCaseModel scenarioCase : getScenarioCases() ) {
            scenarioCase.accept( visitor );
        }
        visitor.visitEnd( this );
    }

    public void addCase( ScenarioCaseModel scenarioCase ) {
        scenarioCase.setCaseNr(scenarioCases.size() + 1);
        scenarioCases.add( scenarioCase );
    }

    public ExecutionStatus getExecutionStatus() {
        if( executionStatus == null ) {
            ExecutionStatusCalculator executionStatusCalculator = new ExecutionStatusCalculator();
            this.accept( executionStatusCalculator );
            executionStatus = executionStatusCalculator.executionStatus();
        }
        return executionStatus;
    }

    public ScenarioCaseModel getCase( int i ) {
        return scenarioCases.get( i );
    }

    public void addTag( Tag tag ) {
        tags.add( tag );
    }

    public void addTags( List<Tag> tags ) {
        this.tags.addAll( tags );
    }

    public void addParameterNames( String... params ) {
        explicitParameters.addAll( Arrays.asList( params ) );
    }

    public void setExplicitParameters( List<String> params ) {
        explicitParameters.clear();
        explicitParameters.addAll( params );
    }

    public List<String> getExplicitParameters() {
        return Collections.unmodifiableList( explicitParameters );
    }

    public List<ScenarioCaseModel> getScenarioCases() {
        return scenarioCases;
    }

    public List<Tag> getTags() {
        return Lists.newArrayList( tags );
    }

    public boolean isCasesAsTable() {
        return casesAsTable;
    }

    public void setCasesAsTable( boolean casesAsTable ) {
        this.casesAsTable = casesAsTable;
    }

    public void clearCases() {
        scenarioCases.clear();
    }

    public long getDurationInNanos() {
        return durationInNanos;
    }

    public void setDurationInNanos( long durationInNanos ) {
        this.durationInNanos = durationInNanos;
    }

    public void addDurationInNanos( long durationInNanosDelta ) {
        this.durationInNanos += durationInNanosDelta;
    }

    public void addDerivedParameter( String parameterName ) {
        this.derivedParameters.add( parameterName );
    }

    public List<String> getDerivedParameters() {
        return Collections.unmodifiableList( derivedParameters );
    }

    public String getClassName() {
        return className;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    public String getTestMethodName() {
        return testMethodName;
    }

    public void setTestMethodName( String testMethodName ) {
        this.testMethodName = testMethodName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public void setTags( Set<Tag> tags ) {
        this.tags = tags;
    }

    public boolean isNotImplementedYet() {
        return notImplementedYet;
    }

    public void setNotImplementedYet( boolean notImplementedYet ) {
        this.notImplementedYet = notImplementedYet;
    }

}