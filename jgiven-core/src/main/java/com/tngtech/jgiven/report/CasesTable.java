package com.tngtech.jgiven.report;

import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents multiple scenario cases as a table structure for simpler reporting.
 */
public class CasesTable {
    private final ScenarioModel scenarioModel;

    public CasesTable(ScenarioModel scenarioModel) {
        this.scenarioModel = scenarioModel;
    }

    /**
     * The placeholders of the data table.
     */
    public List<String> placeholders() {
        List<String> placeHoldersList = new ArrayList<>(scenarioModel.getDerivedParameters());
        List<ScenarioCaseModel> scenarioCases = scenarioModel.getScenarioCases();
        if (!scenarioCases.isEmpty() && scenarioCases.get(0).hasDescription()) {
            placeHoldersList.add(0, "Description");
        }
        return placeHoldersList;
    }

    /**
     * The rows of the table, not including the header.
     */
    public List<Row> rows() {
        List<Row> rows = new ArrayList<>();
        for (ScenarioCaseModel caseModel : scenarioModel.getScenarioCases()) {
            rows.add(new Row(caseModel));
        }
        return rows;
    }

    /**
     * Represents one case of a scenario.
     */
    public static class Row {

        private final ScenarioCaseModel caseModel;

        public Row(ScenarioCaseModel caseModel) {
            this.caseModel = caseModel;
        }

        /**
         * The row number starting from 1.
         */
        public int nr() {
            return caseModel.getCaseNr();
        }

        /**
         * The execution status of the case.
         */
        public ExecutionStatus status() {
            return caseModel.getExecutionStatus();
        }

        /**
         * The argument values of the case.
         */
        public List<String> arguments() {
            List<String> arguments = new ArrayList<>(caseModel.getDerivedArguments());
            if (caseModel.hasDescription()) {
                arguments.add(0, caseModel.getDescription());
            }
            return arguments;
        }

        /**
         *  The error message of the case if any.
         */
        public String errorMessage() {
            return caseModel.getErrorMessage();
        }

        /**
         *  The stack trace of the case if any.
         */
        public List<String> stackTrace() {
            return caseModel.getStackTrace();
        }
    }
}
