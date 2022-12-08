package com.tngtech.jgiven.report.asciidoc;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.report.ReportModelHandler;
import com.tngtech.jgiven.report.ScenarioDataTable;
import com.tngtech.jgiven.report.model.*;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class AsciiDocWriter extends ReportModelVisitor {

    private final ReportModelHandler handler;
    private boolean isMultiCase;
    private boolean hasDataTable;
    private ScenarioModel currentScenarioModel;
    private boolean skipCase;
    private Map<String, Tag> reportModelTagMap;
    private boolean unsuccesfulCase;

    public AsciiDocWriter(ReportModelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void visit(ReportModel reportModel) {
        Optional<String> maybeName = Optional.ofNullable(reportModel.getName());
        handler.className(maybeName.filter(name -> !name.isEmpty()).orElse(reportModel.getClassName()));

        final int totalScenarioCount = reportModel.getScenarios().size();
        final int failedScenarioCount = reportModel.getFailedScenarios().size();
        final int pendingScenarioCount = reportModel.getPendingScenarios().size();
        final int successfulScenarioCount = totalScenarioCount - failedScenarioCount - pendingScenarioCount;
        final Duration duration = Duration.ofNanos(reportModel.getScenarios().stream().mapToLong(ScenarioModel::getDurationInNanos).sum());
        handler.reportSummary(successfulScenarioCount, failedScenarioCount, pendingScenarioCount,
                totalScenarioCount, duration);

        if (reportModel.getDescription() != null) {
            handler.reportDescription(reportModel.getDescription());
        }

        reportModelTagMap = reportModel.getTagMap();
    }

    @Override
    public void visit(ScenarioModel scenarioModel) {
        Set<String> tagNames = scenarioModel.getTagIds().stream()
                .map(this.reportModelTagMap::get)
                .map(Tag::getName).collect(Collectors.toSet());

        handler.scenarioTitle(scenarioModel.getDescription(), scenarioModel.getExtendedDescription(),
                scenarioModel.getExecutionStatus(), Duration.ofNanos(scenarioModel.getDurationInNanos()),
                tagNames);

        this.currentScenarioModel = scenarioModel;
        this.isMultiCase = scenarioModel.getScenarioCases().size() > 1;
        this.hasDataTable = scenarioModel.isCasesAsTable();
    }

    @Override
    public void visitEnd(ScenarioModel scenarioModel) {
        if (hasDataTable) {
            handler.dataTable(new ScenarioDataTableImpl(scenarioModel));
        }
        handler.scenarioEnd();
    }

    @Override
    public void visit(ScenarioCaseModel scenarioCase) {
        if (scenarioCase.getCaseNr() > 1 && hasDataTable) {
            this.skipCase = true;
            return;
        }
        this.skipCase = false;
        this.unsuccesfulCase = scenarioCase.getExecutionStatus() != ExecutionStatus.SUCCESS;

        if (isMultiCase && !hasDataTable) {
            handler.caseHeader(scenarioCase.getCaseNr(),
                    currentScenarioModel.getExplicitParameters(), scenarioCase.getExplicitArguments());
        }
        handler.caseHeader();
    }

    @Override
    public void visit(StepModel stepModel) {
        if (skipCase) {
            return;
        }

        if (stepModel.isSectionTitle()) {
            handler.sectionTitle(stepModel.getName());
            return;
        }

        handler.stepStart(stepModel.getDepth());

        boolean lastWordWasDataTable = false;
        for (Word word : stepModel.getWords()) {
            lastWordWasDataTable = false;
            if (word.isIntroWord()) {
                handler.introWord(word.getValue());
            } else if (word.isArg()) {
                if (word.getArgumentInfo().isParameter()) {
                    if (hasDataTable) {
                        handler.stepArgumentPlaceHolder(word.getArgumentInfo().getParameterName());
                    } else {
                        handler.stepCaseArgument(word.getFormattedValue());
                    }
                } else {
                    if (word.getArgumentInfo().isDataTable()) {
                        handler.stepDataTableArgument(word.getArgumentInfo().getDataTable());
                        lastWordWasDataTable = true;
                    } else {
                        handler.stepArgument(word.getFormattedValue(), word.isDifferent());
                    }
                }
            } else {
                handler.stepWord(word.getFormattedValue(), word.isDifferent());
            }
        }
        if (this.unsuccesfulCase) {
            handler.stepEnd(lastWordWasDataTable, stepModel.getStatus(), Duration.ofNanos(stepModel.getDurationInNanos()), stepModel.getExtendedDescription());
        } else {
            handler.stepEnd(lastWordWasDataTable, stepModel.getExtendedDescription());
        }

    }

    private static class ScenarioDataTableImpl implements ScenarioDataTable {
        private final ScenarioModel scenarioModel;

        private ScenarioDataTableImpl(ScenarioModel scenarioModel) {
            this.scenarioModel = scenarioModel;
        }

        @Override
        public List<String> placeHolders() {
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
                rows.add(new ScenarioDataTableImpl.RowImpl(caseModel));
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
        }
    }
}
