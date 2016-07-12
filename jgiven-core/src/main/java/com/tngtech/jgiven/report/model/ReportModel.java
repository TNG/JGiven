package com.tngtech.jgiven.report.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.AsProvider;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.impl.params.DefaultAsProvider;
import com.tngtech.jgiven.impl.util.AssertionUtil;
import com.tngtech.jgiven.impl.util.ReflectionUtil;

public class ReportModel {
    /**
     * Full qualified name of the test class.
     */
    private String className;

    /**
     * An optional name to group scenarios
     */
    private String name;

    /**
     * An optional description of the test class.
     */
    private String description;

    private List<ScenarioModel> scenarios = Lists.newArrayList();

    private Map<String, Tag> tagMap = Maps.newLinkedHashMap();

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

    public synchronized void addScenarioModel( ScenarioModel currentScenarioModel ) {
        scenarios.add( currentScenarioModel );
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
        return getScenariosWithStatus( ExecutionStatus.SCENARIO_PENDING, ExecutionStatus.SOME_STEPS_PENDING );
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

    public synchronized void addTag( Tag tag ) {
        this.tagMap.put( tag.toIdString(), tag );
    }

    public synchronized void addTags( List<Tag> tags ) {
        for( Tag tag : tags ) {
            addTag( tag );
        }
    }

    public synchronized Tag getTagWithId( String tagId ) {
        Tag tag = this.tagMap.get( tagId );
        AssertionUtil.assertNotNull( tag, "Could not find tag with id " + tagId );
        return tag;
    }

    public synchronized Map<String, Tag> getTagMap() {
        return tagMap;
    }

    public synchronized void setTagMap( Map<String, Tag> tagMap ) {
        this.tagMap = tagMap;
    }

    public synchronized void addScenarioModelOrMergeWithExistingOne( ScenarioModel scenarioModel ) {
        scenarioModel.setClassName( getClassName() );
        Optional<ScenarioModel> existingScenarioModel = findScenarioModel( scenarioModel.getDescription() );

        if( existingScenarioModel.isPresent() ) {
            AssertionUtil.assertTrue( scenarioModel.getScenarioCases().size() == 1, "ScenarioModel has more than one case" );
            existingScenarioModel.get().addCase( scenarioModel.getCase( 0 ) );
            existingScenarioModel.get().addDurationInNanos( scenarioModel.getDurationInNanos() );
        } else {
            addScenarioModel( scenarioModel );
        }
    }

    public synchronized void setTestClass( Class<?> testClass ) {
        AssertionUtil.assertTrue( className == null || testClass.getName().equals( className ),
            "Test class of the same report model was set to different values. 1st value: " + className +
                    ", 2nd value: " + testClass.getName() );
        setClassName( testClass.getName() );
        if( testClass.isAnnotationPresent( Description.class ) ) {
            setDescription( testClass.getAnnotation( Description.class ).value() );
        }

        As as = testClass.getAnnotation( As.class );
        AsProvider provider = as != null
                ? ReflectionUtil.newInstance( as.provider() )
                : new DefaultAsProvider();
        name = provider.as( as, testClass );
    }

    public String getName() {
        return name;
    }
}
