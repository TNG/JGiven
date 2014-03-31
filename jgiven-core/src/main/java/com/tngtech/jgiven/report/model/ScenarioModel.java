package com.tngtech.jgiven.report.model;

import java.util.List;

import com.google.common.collect.Lists;

public class ScenarioModel {
    public String className;
    public String testMethodName;
    public String description;
    public List<Tag> tags = Lists.newArrayList();
    public List<String> parameterNames = Lists.newArrayList();

    public List<ScenarioCaseModel> scenarioCases = Lists.newArrayList();

    public void accept( ReportModelVisitor visitor ) {
        visitor.visit( this );
        for( ScenarioCaseModel scenarioCase : scenarioCases ) {
            scenarioCase.accept( visitor );
        }
        visitor.visitEnd( this );
    }

    public void addCase( ScenarioCaseModel scenarioCase ) {
        scenarioCase.caseNr = scenarioCases.size() + 1;
        scenarioCases.add( scenarioCase );
    }

    public ImplementationStatus getImplementationStatus() {
        return new ReportModelVisitor() {
            int implementedCount;
            int notImplementedCount;

            @Override
            public void visit( StepModel stepModel ) {
                if( stepModel.notImplementedYet ) {
                    notImplementedCount++;
                } else {
                    implementedCount++;
                }
            };

            public ImplementationStatus notFullyImplementedYet() {
                ScenarioModel.this.accept( this );
                if( implementedCount == 0 )
                    return ImplementationStatus.NONE;
                if( notImplementedCount == 0 )
                    return ImplementationStatus.FINISHED;
                return ImplementationStatus.PARTIALLY;
            }

        }.notFullyImplementedYet();
    }

    public ScenarioCaseModel getCase( int i ) {
        return scenarioCases.get( i );
    }

    public void addTag( Tag tag ) {
        tags.add( tag );
    }

}