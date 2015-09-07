package com.tngtech.jgiven.report.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public class ScenarioCaseModel {
    /**
     * The number of the case starting with 0
     */
    private int caseNr;

    /**
     * The steps of this case
     */
    private List<StepModel> steps = Lists.newArrayList();

    /**
     * The arguments that have been explicitly passed to a scenario test.
     * These arguments only appear in a report if there are multiple cases
     * and no data table could be written.
     */
    private List<String> explicitArguments = Lists.newArrayList();

    /**
     * Derived arguments are arguments that used as arguments to step methods.
     * These have not to be the same as the explicit arguments.
     * However, typically they are somehow derived from them.
     * For data tables only the derived arguments are used, because
     * these are the only visible arguments.
     */
    private List<String> derivedArguments = Lists.newArrayList();

    public boolean success = true;
    public String errorMessage;

    /**
     * The total execution time of the whole case in nanoseconds.
     */
    public long durationInNanos;

    public void accept( ReportModelVisitor visitor ) {
        visitor.visit( this );
        for( StepModel step : getSteps() ) {
            step.accept( visitor );
        }
        visitor.visitEnd( this );
    }

    public void addExplicitArguments( String... args ) {
        explicitArguments.addAll( Arrays.asList( args ) );
    }

    public void setExplicitArguments( List<String> arguments ) {
        explicitArguments.clear();
        explicitArguments.addAll( arguments );
    }

    public List<String> getExplicitArguments() {
        return Collections.unmodifiableList( explicitArguments );
    }

    public void addStep( StepModel stepModel ) {
        steps.add( stepModel );
    }

    public StepModel getStep( int i ) {
        return steps.get( i );
    }

    public List<StepModel> getSteps() {
        return Collections.unmodifiableList( steps );
    }

    public void setDurationInNanos( long durationInNanos ) {
        this.durationInNanos = durationInNanos;
    }

    public long getDurationInNanos() {
        return durationInNanos;
    }

    public ExecutionStatus getExecutionStatus() {
        ExecutionStatusCalculator executionStatusCalculator = new ExecutionStatusCalculator();
        this.accept( executionStatusCalculator );
        return executionStatusCalculator.executionStatus();
    }

    public void addDerivedArguments( String... values ) {
        this.derivedArguments.addAll( Arrays.asList( values ) );
    }

    public List<String> getDerivedArguments() {
        return Collections.unmodifiableList( derivedArguments );
    }

    public int getCaseNr() {
        return caseNr;
    }

    public void setCaseNr( int caseNr ) {
        this.caseNr = caseNr;
    }

    public void setSteps( List<StepModel> steps ) {
        this.steps = steps;
    }

    public StepModel getFirstStep() {
        return steps.get( 0 );
    }
}