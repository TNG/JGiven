package com.tngtech.jgiven.impl.intercept;

import com.tngtech.jgiven.report.model.StepStatus;

public enum InvocationMode {
    NORMAL,
    FAILED,
    SKIPPED,
    NOT_IMPLEMENTED_YET;

    public StepStatus toStepStatus() {
        switch( this ) {
            case NORMAL:
                return StepStatus.PASSED;
            case FAILED:
                return StepStatus.FAILED;
            case NOT_IMPLEMENTED_YET:
                return StepStatus.NOT_IMPLEMENTED_YET;
            case SKIPPED:
                return StepStatus.SKIPPED;
            default:
                throw new IllegalArgumentException( this.toString() );
        }
    }
}
