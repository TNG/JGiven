package com.tngtech.jgiven.report.model;

final class ExecutionStatusCalculator extends ReportModelVisitor {
    private int failedCount;
    private int pendingCount;
    private int totalCount;
    private ExecutionStatus status;

    @Override
    public void visit( StepModel stepModel ) {
        if( stepModel.isFailed() ) {
            failedCount++;
        } else if( stepModel.isPending() ) {
            pendingCount++;
        }
        totalCount++;
    }

    public ExecutionStatus executionStatus() {
        if( status != null ) {
            return status;
        }

        if( failedCount > 0 ) {
            return ExecutionStatus.FAILED;
        }

        if( pendingCount > 0 ) {
            if( pendingCount < totalCount ) {
                return ExecutionStatus.SOME_STEPS_PENDING;
            }
            return ExecutionStatus.SCENARIO_PENDING;
        }

        return ExecutionStatus.SUCCESS;
    }
}