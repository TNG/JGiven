package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterStage;

public class FailingAfterStageMethodStage extends Stage<FailingAfterStageMethodStage> {

    @AfterStage
    public void afterStage() {
        throw new RuntimeException( "Failed After Stage Method" );
    }

    public FailingAfterStageMethodStage nothing() {
        return this;
    }
}
