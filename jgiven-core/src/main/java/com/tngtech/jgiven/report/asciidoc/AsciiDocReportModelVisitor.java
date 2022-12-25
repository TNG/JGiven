package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.*;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class AsciiDocReportModelVisitor extends ReportModelVisitor {

    private final ReportBlockConverter handler;
    private boolean isMultiCase;
    private boolean hasDataTable;
    private ScenarioModel currentScenarioModel;
    private boolean skipCase;
    private Map<String, Tag> reportModelTagMap;
    private boolean unsuccesfulCase;

    public AsciiDocReportModelVisitor(ReportBlockConverter handler) {
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

    @Override
    public void visitEnd(ScenarioCaseModel scenarioCase) {
        super.visitEnd(scenarioCase);
    }

    @Override
    public void visitEnd(ScenarioModel scenarioModel) {
        if (hasDataTable) {
            handler.dataTable(new ScenarioDataTableImpl(scenarioModel));
        }
        handler.scenarioEnd();
    }

    @Override
    public void visitEnd(ReportModel testCaseModel) {
        super.visitEnd(testCaseModel);
    }

}
