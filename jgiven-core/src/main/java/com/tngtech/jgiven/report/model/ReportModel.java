package com.tngtech.jgiven.report.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ReportModel {
    /**
     * Full qualified name of the test class.
     */
    private String className;

    /**
     * An optional description of the test class.
     */
    private String description;

    private List<ScenarioModel> scenarios = Lists.newArrayList();

    public void accept( ReportModelVisitor visitor ) {
        visitor.visit( this );
        List<ScenarioModel> sorted = sortByDescription();
        for( ScenarioModel m : sorted ) {
            m.accept( visitor );
        }
        visitor.visitEnd( this );

    }

    private List<ScenarioModel> sortByDescription() {
        List<ScenarioModel> sorted = Lists.newArrayList( getScenarios() );
        Collections.sort( sorted, new Comparator<ScenarioModel>() {
            @Override
            public int compare( ScenarioModel o1, ScenarioModel o2 ) {
                return o1.description.toLowerCase().compareTo( o2.description.toLowerCase() );
            }
        } );
        return sorted;
    }

    public ScenarioModel getLastScenarioModel() {
        return getScenarios().get( getScenarios().size() - 1 );
    }

    public Optional<ScenarioModel> findScenarioModel( String scenarioDescription ) {
        for( ScenarioModel model : getScenarios() ) {
            if( model.description.equals( scenarioDescription ) ) {
                return Optional.of( model );
            }
        }
        return Optional.absent();
    }

    public StepModel getFirstStepModelOfLastScenario() {
        return getLastScenarioModel().getCase( 0 ).steps.get( 0 );
    }

    public void addScenarioModel( ScenarioModel currentScenarioModel ) {
        getScenarios().add( currentScenarioModel );
    }

    public String getSimpleClassName() {
        return Iterables.getLast( Splitter.on( '.' ).split( getClassName() ) );
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    public List<ScenarioModel> getScenarios() {
        return scenarios;
    }

    public void setScenarios( List<ScenarioModel> scenarios ) {
        this.scenarios = scenarios;
    }

}
