package com.tngtech.jgiven.report.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculates a table representation for all cases of a scenario.
 */
public class CasesTableCalculator {

    /**
     * Collect cases form this scenario.
     *
     * @param scenario the scenario to be analyzed
     * @return the table representation for all cases
     */
    public CasesTable collectCases(final ScenarioModel scenario) {
        final CasesTableVisitor casesTableVisitor = new CasesTableVisitor();
        scenario.accept(casesTableVisitor);
        return casesTableVisitor.getTable();
    }

    private static class CasesTableVisitor extends ReportModelVisitor {
        private final List<String> placeholders = new ArrayList<>();
        private boolean withDescriptions = false;
        private final List<CasesTable.CaseRow> caseRows = new ArrayList<>();

        @Override public void visit(final ScenarioModel scenarioModel) {
            super.visit(scenarioModel);
            placeholders.addAll(scenarioModel.getDerivedParameters());
        }

        @Override public void visit(final ScenarioCaseModel scenarioCase) {
            super.visit(scenarioCase);
            if (scenarioCase.hasDescription()) {
                withDescriptions = true;
            }
            final CasesTable.CaseRow caseRow = new CasesTable.CaseRow(
                    scenarioCase.getCaseNr(),
                    scenarioCase.getDescription(),
                    scenarioCase.getDerivedArguments(),
                    scenarioCase.getExecutionStatus(),
                    scenarioCase.getDurationInNanos(),
                    scenarioCase.getErrorMessage(),
                    scenarioCase.getStackTrace());
            caseRows.add(caseRow);
        }

        public CasesTable getTable() {
            return new CasesTable(placeholders, withDescriptions, caseRows);
        }
    }
}
