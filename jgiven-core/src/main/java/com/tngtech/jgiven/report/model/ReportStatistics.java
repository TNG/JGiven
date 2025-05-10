package com.tngtech.jgiven.report.model;

public class ReportStatistics {
    public int numClasses;
    public int numScenarios;
    public int numFailedScenarios;
    public int numPendingScenarios;
    public int numAbortedScenarios;
    public int numSuccessfulScenarios;
    public int numCases;
    public int numFailedCases;
    public int numSteps;
    public long durationInNanos;

    public ReportStatistics add( ReportStatistics statistics ) {
        ReportStatistics copy = new ReportStatistics();
        copy.addModifying( this );
        copy.addModifying( statistics );
        return copy;
    }

    private void addModifying( ReportStatistics statistics ) {
        this.numClasses += statistics.numClasses;
        this.numScenarios += statistics.numScenarios;
        this.numFailedScenarios += statistics.numFailedScenarios;
        this.numPendingScenarios += statistics.numPendingScenarios;
        this.numAbortedScenarios += statistics.numAbortedScenarios;
        this.numSuccessfulScenarios += statistics.numSuccessfulScenarios;
        this.numCases += statistics.numCases;
        this.numFailedCases += statistics.numFailedCases;
        this.numSteps += statistics.numSteps;
        this.durationInNanos += statistics.durationInNanos;
    }
}
