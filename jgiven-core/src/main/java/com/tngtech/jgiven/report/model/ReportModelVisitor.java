package com.tngtech.jgiven.report.model;


public class ReportModelVisitor {
    public void visit( ReportModel reportModel ) {}

    public void visit( ScenarioModel scenarioModel ) {}

    public void visit( ScenarioCaseModel scenarioCase ) {}

    public void visit( StepModel stepModel ) {}

    public void visitEnd( ScenarioCaseModel scenarioCase ) {}

    public void visitEnd( ScenarioModel scenarioModel ) {}

    public void visitEnd( ReportModel testCaseModel ) {}
}
