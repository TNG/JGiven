package com.tngtech.jgiven.report.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ReportModel {
    /**
     * Full qualified name of the test class.
     */
    public String className;

    /**
     * An optional description of the test class.
     */
    public String description;

    public List<ScenarioModel> scenarios = Lists.newArrayList();

    public void accept( ReportModelVisitor visitor ) {
        visitor.visit( this );
        List<ScenarioModel> sorted = sortByDescription();
        for( ScenarioModel m : sorted ) {
            m.accept( visitor );
        }
        visitor.visitEnd( this );

    }

    private List<ScenarioModel> sortByDescription() {
        List<ScenarioModel> sorted = Lists.newArrayList( scenarios );
        Collections.sort( sorted, new Comparator<ScenarioModel>() {
            @Override
            public int compare( ScenarioModel o1, ScenarioModel o2 ) {
                return o1.description.compareTo( o2.description );
            }
        } );
        return sorted;
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
