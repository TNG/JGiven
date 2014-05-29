package com.tngtech.jgiven.report.model;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class StepModel {
    public String name;
    public List<Word> words = Lists.newArrayList();
    private StepStatus status = StepStatus.PASSED;

    public void accept( ReportModelVisitor visitor ) {
        visitor.visit( this );
    }

    public String getCompleteSentence() {
        return Joiner.on( ' ' ).join( words );
    }

    public StepModel addWords( Word... words ) {
        this.words.addAll( Arrays.asList( words ) );
        return this;
    }

    public boolean isNotImplementedYet() {
        return getStatus() == StepStatus.NOT_IMPLEMENTED;
    }

    public boolean isFailed() {
        return getStatus() == StepStatus.FAILED;
    }

    public boolean isSkipped() {
        return getStatus() == StepStatus.SKIPPED;
    }

    public StepStatus getStatus() {
        return status;
    }

    public void setStatus( StepStatus status ) {
        this.status = status;
    }
}