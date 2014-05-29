package com.tngtech.jgiven.report.model;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.impl.intercept.InvocationMode;

public class ScenarioCaseModel {
    public int caseNr;
    public List<StepModel> steps = Lists.newArrayList();
    public List<String> arguments = Lists.newArrayList();
    public boolean success = true;
    public String errorMessage;

    public StepModel addStep( String name, List<Word> words, InvocationMode mode ) {
        StepModel stepModel = new StepModel();
        stepModel.name = name;
        stepModel.words = words;
        stepModel.setStatus( toStepStatus( mode ) );
        steps.add( stepModel );
        return stepModel;
    }

    private StepStatus toStepStatus( InvocationMode mode ) {
        switch( mode ) {
            case NORMAL:
                return StepStatus.PASSED;
            case FAILED:
                return StepStatus.FAILED;
            case NOT_IMPLEMENTED_YET:
                return StepStatus.NOT_IMPLEMENTED;
            case SKIPPED:
                return StepStatus.SKIPPED;
            default:
                throw new IllegalArgumentException( mode.toString() );
        }
    }

    public void accept( ReportModelVisitor visitor ) {
        visitor.visit( this );
        for( StepModel step : steps ) {
            step.accept( visitor );
        }
        visitor.visitEnd( this );
    }

    public void addArguments( String... args ) {
        arguments.addAll( Arrays.asList( args ) );
    }

    public void addStep( StepModel stepModel ) {
        steps.add( stepModel );
    }

    public StepModel getStep( int i ) {
        return steps.get( i );
    }
}