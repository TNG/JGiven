package com.tngtech.jgiven.report.model;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ScenarioModel {
    public String className;
    public String testMethodName;
    public String description;
    public Set<Tag> tags = Sets.newLinkedHashSet();
    public boolean notImplementedYet;
    public List<String> parameterNames = Lists.newArrayList();
    private boolean casesAsTable;
    private final List<ScenarioCaseModel> scenarioCases = Lists.newArrayList();
    private long durationInNanos;

    public void accept( ReportModelVisitor visitor ) {
        visitor.visit( this );
        for( ScenarioCaseModel scenarioCase : getScenarioCases() ) {
            scenarioCase.accept( visitor );
        }
        visitor.visitEnd( this );
    }

    public void addCase( ScenarioCaseModel scenarioCase ) {
        scenarioCase.caseNr = scenarioCases.size() + 1;
        scenarioCases.add( scenarioCase );
    }

    public ExecutionStatus getExecutionStatus() {
        ExecutionStatusCalculator executionStatusCalculator = new ExecutionStatusCalculator();
        this.accept( executionStatusCalculator );
        return executionStatusCalculator.executionStatus();
    }

    public ScenarioCaseModel getCase( int i ) {
        return scenarioCases.get( i );
    }

    public void addTag( Tag tag ) {
        tags.add( tag );
    }

    public void addParameterNames( String... params ) {
        parameterNames.addAll( Arrays.asList( params ) );
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
}