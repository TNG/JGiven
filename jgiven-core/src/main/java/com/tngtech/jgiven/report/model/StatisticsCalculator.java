package com.tngtech.jgiven.report.model;

import java.util.List;

public class StatisticsCalculator {

    public ReportStatistics getStatistics( ReportModel model ) {
        ReportStatistics result = new ReportStatistics();
        model.accept( new StatisticsVisitor( result ) );
        return result;
    }

    public ReportStatistics getStatistics( List<ScenarioModel> models ) {
        ReportStatistics result = new ReportStatistics();
        for( ScenarioModel model : models ) {
            model.accept( new StatisticsVisitor( result ) );
        }
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
        }

        @Override
        public void visit( ScenarioCaseModel scenarioCase ) {
            statistics.numCases++;
            if( !scenarioCase.success ) {
                statistics.numFailedCases++;
            }
        }

        @Override
        public void visit( StepModel methodModel ) {
            statistics.numSteps++;
        }

    }

}
