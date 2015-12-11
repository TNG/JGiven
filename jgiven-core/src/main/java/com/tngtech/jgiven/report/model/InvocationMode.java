package com.tngtech.jgiven.report.model;

public enum InvocationMode {
    NORMAL,
    NESTED,
    FAILED,
    SKIPPED,
    PENDING,
    DO_NOT_INTERCEPT;

    public StepStatus toStepStatus() {
        switch( this ) {
            case NORMAL:
                return StepStatus.PASSED;
            case FAILED:
                return StepStatus.FAILED;
            case PENDING:
                return StepStatus.PENDING;
            case SKIPPED:
                return StepStatus.SKIPPED;
            default:
                throw new IllegalArgumentException( this.toString() );
        }
    }
}
