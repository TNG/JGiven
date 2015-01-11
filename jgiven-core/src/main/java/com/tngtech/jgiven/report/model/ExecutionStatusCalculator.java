package com.tngtech.jgiven.report.model;

final class ExecutionStatusCalculator extends ReportModelVisitor {
    private int failedCount;
    private int notImplementedCount;
    private int totalCount;
    private ExecutionStatus status;

    @Override
    public void visit( ScenarioModel scenarioModel ) {
        if(scenarioModel.isNotImplementedYet()) {
            status = ExecutionStatus.NONE_IMPLEMENTED;
        }
    }

    @Override
    public void visit( ScenarioCaseModel scenarioCase ) {
        if( !scenarioCase.success ) {
            status = ExecutionStatus.FAILED;
        }
    }

    @Override
    public void visit( StepModel stepModel ) {
        if( stepModel.isFailed() ) {
            failedCount++;
        } else if( stepModel.isNotImplementedYet() ) {
            notImplementedCount++;
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

        if( notImplementedCount > 0 ) {
            if( notImplementedCount < totalCount ) {
                return ExecutionStatus.PARTIALLY_IMPLEMENTED;
            }
            return ExecutionStatus.NONE_IMPLEMENTED;
        }

        return ExecutionStatus.SUCCESS;
    }
}