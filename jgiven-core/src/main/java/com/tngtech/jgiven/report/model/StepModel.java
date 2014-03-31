package com.tngtech.jgiven.report.model;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class StepModel {
    public String name;
    public List<Word> words = Lists.newArrayList();
    public List<String> parameterNames = Lists.newArrayList();
    public boolean notImplementedYet;
    public boolean failed;
    public boolean skipped;

    public void accept( ReportModelVisitor visitor ) {
        visitor.visit( this );
    }

    public String getCompleteSentence() {
        return Joiner.on( ' ' ).join( words );
    }
}