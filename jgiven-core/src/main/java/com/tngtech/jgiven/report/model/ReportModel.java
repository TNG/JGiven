package com.tngtech.jgiven.report.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
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
                return o1.getDescription().toLowerCase().compareTo( o2.getDescription().toLowerCase() );
            }
        } );
        return sorted;
    }

    public ScenarioModel getLastScenarioModel() {
        return getScenarios().get( getScenarios().size() - 1 );
    }

    public Optional<ScenarioModel> findScenarioModel( String scenarioDescription ) {
        for( ScenarioModel model : getScenarios() ) {
            if( model.getDescription().equals( scenarioDescription ) ) {
                return Optional.of( model );
            }
        }
        return Optional.absent();
    }

    public StepModel getFirstStepModelOfLastScenario() {
        return getLastScenarioModel().getCase( 0 ).getStep( 0 );
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

    public String getPackageName() {
        int index = this.className.lastIndexOf( '.' );
        if( index == -1 ) {
            return "";
        }
        return this.className.substring( 0, index );
    }

    public List<ScenarioModel> getFailedScenarios() {
        return getScenariosWithStatus( ExecutionStatus.FAILED );
    }

    public List<ScenarioModel> getPendingScenarios() {
        return getScenariosWithStatus( ExecutionStatus.NONE_IMPLEMENTED, ExecutionStatus.PARTIALLY_IMPLEMENTED );
    }

    public List<ScenarioModel> getScenariosWithStatus( ExecutionStatus first, ExecutionStatus... rest ) {
        EnumSet<ExecutionStatus> stati = EnumSet.of( first, rest );
        List<ScenarioModel> result = Lists.newArrayList();
        for( ScenarioModel m : scenarios ) {
            ExecutionStatus executionStatus = m.getExecutionStatus();
            if( stati.contains( executionStatus ) ) {
                result.add( m );
            }
        }
        return result;
    }

}
