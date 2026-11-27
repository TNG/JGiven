package com.tngtech.jgiven.examples.assumptions;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.NestedSteps;
import org.junit.Assume;

public class AssumptionFailureTestStage extends Stage<AssumptionFailureTestStage> {

    public AssumptionFailureTestStage nothing() {
        return self();
    }

    @SuppressWarnings("DataFlowIssue") //fail on purpose
    public AssumptionFailureTestStage a_failed_junit_assumption() {
        Assume.assumeFalse(true);
        return self();
    }

    @NestedSteps
    public AssumptionFailureTestStage a_failed_nested_step() {
        self().a_failed_junit_assumption();
        return self();
    }
}
