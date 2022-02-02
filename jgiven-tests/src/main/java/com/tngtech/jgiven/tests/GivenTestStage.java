package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.Stage;

public class GivenTestStage extends Stage<GivenTestStage> {
    public GivenTestStage an_exception_is_thrown() {
        throw new RuntimeException("Some Exception");
    }

    public GivenTestStage nothing() {
        return this;
    }

    public GivenTestStage a_failed_step(boolean fail) {
        if (fail) {
            throw new IllegalArgumentException();
        }
        return this;
    }


}