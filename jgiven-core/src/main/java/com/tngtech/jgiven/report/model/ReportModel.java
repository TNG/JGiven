package com.tngtech.jgiven.report.model;

import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ReportModel {
    /**
     * Full qualified name of the test class
     */
    public String className;

    /**
     * An optional description of the test class
     */
    public String description;

    public List<ScenarioModel> scenarios = Lists.newArrayList();

    public void accept( ReportModelVisitor visitor ) {
        visitor.visit( this );
        for( ScenarioModel m : scenarios ) {
            m.accept( visitor );
        }
        visitor.visitEnd( this );

    }

    public ScenarioModel getLastScenarioModel() {
        return scenarios.get( scenarios.size() - 1 );
    }

    public StepModel getFirstStepModelOfLastScenario() {
        return getLastScenarioModel().getCase( 0 ).steps.get( 0 );
    }

    public void addScenarioModel( ScenarioModel currentScenarioModel ) {
        scenarios.add( currentScenarioModel );
    }

    public String getSimpleClassName() {
        return Iterables.getLast( Splitter.on( '.' ).split( className ) );
    }

}
