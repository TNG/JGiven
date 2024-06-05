package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.Stage;
import org.junit.jupiter.api.Assumptions;
import org.testng.SkipException;

import static org.assertj.core.api.Assumptions.assumeThat;

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
        return self();
    }

    public GivenTestStage a_failed_assertJ_assumption() {
        assumeThat(true).isFalse();
        return self();
    }

    public GivenTestStage a_failed_junit_assumption() {
        Assumptions.assumeFalse(true);
        return self();
    }

    public GivenTestStage a_failed_testng_assumption(){
        throw new SkipException("Fail on purpose");
    }


}