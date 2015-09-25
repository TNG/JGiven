package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.Stage;

public class GivenTestStage extends Stage<GivenTestStage> {
    public GivenTestStage an_exception_is_thrown() {
        throw new RuntimeException("Some Exception");
    }

    public void nothing() {
    }

    public void a_failed_step(boolean fail) {
        if (fail) {
            throw new IllegalArgumentException();
        }
    }


}