package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.NestedSteps;
import org.junit.Assume;
import org.junit.jupiter.api.Assumptions;
import org.testng.SkipException;

import static org.assertj.core.api.Assumptions.assumeThat;

public class GivenTestStage extends Stage<GivenTestStage> {
    public GivenTestStage an_exception_is_thrown() {
        throw new RuntimeException("Some Exception");
    }

    public GivenTestStage nothing() {
        return self();
    }

    public GivenTestStage a_failed_step(boolean fail) {
        if (fail) {
            throw new IllegalArgumentException();
        }
        return self();
    }

    @SuppressWarnings("DataFlowIssue") //fail on purpose
    public GivenTestStage a_failed_junit5_assumption() {
        Assumptions.assumeFalse(true);
        return self();
    }

    @SuppressWarnings("DataFlowIssue") //fail on purpose
    public GivenTestStage a_failed_testng_assumption(){
        throw new SkipException("Fail on purpose");
    }


    @SuppressWarnings("DataFlowIssue") //fail on purpose
    public GivenTestStage a_failed_junit_assumption() {
        Assume.assumeFalse(true);
        return self();
    }
}
