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
        return new ReportModelVisitor() {
            int failedCount;
            int notImplementedCount;
            int totalCount;

            @Override
            public void visit( StepModel stepModel ) {
                if( stepModel.isFailed() ) {
                    failedCount++;
                } else if( stepModel.isNotImplementedYet() ) {
                    notImplementedCount++;
                }
                totalCount++;
            };

            public ExecutionStatus excecutionStatus() {
                if( ScenarioModel.this.notImplementedYet ) {
                    return ExecutionStatus.NONE_IMPLEMENTED;
                }

                ScenarioModel.this.accept( this );
                if( failedCount > 0 ) {
                    return ExecutionStatus.FAILED;
                }

                if( notImplementedCount > 0 ) {
                    if( notImplementedCount < totalCount ) {
                        return ExecutionStatus.PARTIALLY_IMPLEMENTED;
                    }
                    return ExecutionStatus.NONE_IMPLEMENTED;
                }

                return ExecutionStatus.SUCCESS;
            }

        }.excecutionStatus();
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
}