package com.tngtech.jgiven.report.model;


public class ReportModelVisitor {
    public void visit( ReportModel reportModel ) {}

    public void visit( ScenarioModel scenarioModel ) {}

    public void visit( StepModel stepModel ) {}

    public void visit( ScenarioCaseModel scenarioCase ) {}

    public void visitEnd( ReportModel testCaseModel ) {}

    public void visitEnd( ScenarioCaseModel scenarioCase ) {}

    public void visitEnd( ScenarioModel scenarioModel ) {}
}
