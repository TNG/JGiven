package com.tngtech.jgiven.report.asciidoc;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.report.CasesTable;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import java.util.ArrayList;
import java.util.List;

class CasesTableImpl implements CasesTable {
    private final ScenarioModel scenarioModel;

    CasesTableImpl(ScenarioModel scenarioModel) {
        this.scenarioModel = scenarioModel;
    }

    @Override
    public List<String> placeholders() {
        List<String> placeHoldersList = new ArrayList<>(scenarioModel.getDerivedParameters());
        List<ScenarioCaseModel> scenarioCases = scenarioModel.getScenarioCases();
        if (!scenarioCases.isEmpty() && scenarioCases.get(0).hasDescription()) {
            placeHoldersList.add(0, "Description");
        }
        return placeHoldersList;
    }

    @Override
    public List<Row> rows() {
        List<Row> rows = Lists.newArrayList();
        for (ScenarioCaseModel caseModel : scenarioModel.getScenarioCases()) {
            rows.add(new RowImpl(caseModel));
        }
        return rows;
    }

    private static class RowImpl implements Row {

        private final ScenarioCaseModel caseModel;

        public RowImpl(ScenarioCaseModel caseModel) {
            this.caseModel = caseModel;
        }

        @Override
        public int nr() {
            return caseModel.getCaseNr();
        }

        @Override
        public ExecutionStatus status() {
            return caseModel.getExecutionStatus();
        }

        @Override
        public List<String> arguments() {
            List<String> arguments = new ArrayList<>(caseModel.getDerivedArguments());
            if (caseModel.hasDescription()) {
                arguments.add(0, caseModel.getDescription());
            }
            return arguments;
        }

        @Override
        public String errorMessage() {
            return caseModel.getErrorMessage();
        }

        @Override
        public List<String> stackTrace() {
            return caseModel.getStackTrace();
        }
    }
}
