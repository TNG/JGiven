package com.tngtech.jgiven.report.model;

public class StatisticsCalculator {

    public ReportStatistics getStatistics( ReportModel model ) {
        ReportStatistics result = new ReportStatistics();
        model.accept( new StatisticsVisitor( result ) );
        return result;
    }

    private static class StatisticsVisitor extends ReportModelVisitor {
        final ReportStatistics statistics;

        public StatisticsVisitor( ReportStatistics statistics ) {
            this.statistics = statistics;
        }

        @Override
        public void visit( ReportModel reportModel ) {
            statistics.numClasses++;
        }

        @Override
        public void visit( ScenarioModel scenarioModel ) {
            statistics.numScenarios++;
            statistics.durationInNanos += scenarioModel.getDurationInNanos();
            ExecutionStatus executionStatus = scenarioModel.getExecutionStatus();
            if( executionStatus == ExecutionStatus.FAILED ) {
                statistics.numFailedScenarios += 1;
            } else if( executionStatus == ExecutionStatus.SCENARIO_PENDING || executionStatus == ExecutionStatus.SOME_STEPS_PENDING) {
                statistics.numPendingScenarios += 1;
            } else {
                statistics.numSuccessfulScenarios += 1;
            }
        }

        @Override
        public void visit( ScenarioCaseModel scenarioCase ) {
            statistics.numCases++;
            if( !scenarioCase.isSuccess()) {
                statistics.numFailedCases++;
            }
        }

        @Override
        public void visit( StepModel stepModel ) {
            statistics.numSteps++;
        }

    }

}
