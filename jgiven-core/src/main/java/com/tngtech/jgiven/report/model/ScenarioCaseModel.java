package com.tngtech.jgiven.report.model;

import java.util.List;

import com.google.common.collect.Lists;

public class ScenarioCaseModel {
    public int caseNr;
    public List<StepModel> steps = Lists.newArrayList();
    public List<String> arguments = Lists.newArrayList();
    public boolean success = true;
    public String errorMessage;

    public StepModel addStep( String name, List<Word> words, boolean notImplementedYet ) {
        StepModel stepModel = new StepModel();
        stepModel.name = name;
        stepModel.words = words;
        stepModel.notImplementedYet = notImplementedYet;
        steps.add( stepModel );
        return stepModel;
    }

    public void accept( ReportModelVisitor visitor ) {
        visitor.visit( this );
        for( StepModel step : steps ) {
            step.accept( visitor );
        }
        visitor.visitEnd( this );
    }
}