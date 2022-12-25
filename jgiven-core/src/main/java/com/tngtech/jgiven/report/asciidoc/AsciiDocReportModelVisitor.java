package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.*;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

class AsciiDocReportModelVisitor extends ReportModelVisitor {

    private final ReportStatistics statistics;
    private final ReportBlockConverter blockConverter;
    private final List<String> asciiDocBlocks;
    private Map<String, Tag> featureTagMap;
    private boolean scenarioHasDataTable;
    private List<String> explicitScenarioParameters;
    private boolean skipCurrentCase;
    private boolean caseIsUnsuccessful;

    public AsciiDocReportModelVisitor(ReportStatistics statistics, ReportBlockConverter blockConverter) {
        this.statistics = statistics;
        this.blockConverter = blockConverter;
        this.asciiDocBlocks = new ArrayList<>();
    }

    @Override
    public void visit(ReportModel reportModel) {
        String featureName = Optional.ofNullable(reportModel.getName())
            .filter(name -> !name.isEmpty())
            .orElse(reportModel.getClassName());

        asciiDocBlocks.add(
            blockConverter.convertFeatureHeaderBlock(featureName, statistics, reportModel.getDescription()));

        featureTagMap = reportModel.getTagMap();
    }

    @Override
    public void visit(ScenarioModel scenarioModel) {
        final List<String> tagNames = scenarioModel.getTagIds().stream()
            .map(this.featureTagMap::get)
            .map(Tag::getName).collect(Collectors.toList());

        asciiDocBlocks.add(blockConverter.convertScenarioHeaderBlock(scenarioModel.getDescription(),
            scenarioModel.getExecutionStatus(), scenarioModel.getDurationInNanos(), tagNames,
            scenarioModel.getExtendedDescription()));

        scenarioHasDataTable = scenarioModel.isCasesAsTable();
        explicitScenarioParameters = scenarioModel.getExplicitParameters();
    }

    @Override
    public void visit(ScenarioCaseModel scenarioCase) {
        skipCurrentCase = scenarioHasDataTable && scenarioCase.getCaseNr() > 1;
        if (skipCurrentCase) {
            return;
        }

        if (!scenarioHasDataTable) {
            asciiDocBlocks.add(blockConverter.convertCaseHeaderBlock(
                scenarioCase.getCaseNr(), explicitScenarioParameters, scenarioCase.getExplicitArguments()));
        }

        caseIsUnsuccessful = scenarioCase.getExecutionStatus() != ExecutionStatus.SUCCESS;
    }

    @Override
    public void visit(StepModel stepModel) {
        if (skipCurrentCase) {
            return;
        }

        if (stepModel.isSectionTitle()) {
            asciiDocBlocks.add(blockConverter.sectionTitle(stepModel.getName()));
            return;
        }

        blockConverter.stepStart(stepModel.getDepth());

        boolean lastWordWasDataTable = false;
        for (Word word : stepModel.getWords()) {
            lastWordWasDataTable = false;
            if (word.isIntroWord()) {
                blockConverter.introWord(word.getValue());
            } else if (word.isArg()) {
                if (word.getArgumentInfo().isParameter()) {
                    if (scenarioHasDataTable) {
                        blockConverter.stepArgumentPlaceHolder(word.getArgumentInfo().getParameterName());
                    } else {
                        blockConverter.stepCaseArgument(word.getFormattedValue());
                    }
                } else {
                    if (word.getArgumentInfo().isDataTable()) {
                        blockConverter.stepDataTableArgument(word.getArgumentInfo().getDataTable());
                        lastWordWasDataTable = true;
                    } else {
                        blockConverter.stepArgument(word.getFormattedValue(), word.isDifferent());
                    }
                }
            } else {
                blockConverter.stepWord(word.getFormattedValue(), word.isDifferent());
            }
        }
        if (this.caseIsUnsuccessful) {
            blockConverter.stepEnd(lastWordWasDataTable, stepModel.getStatus(),
                Duration.ofNanos(stepModel.getDurationInNanos()), stepModel.getExtendedDescription());
        } else {
            blockConverter.stepEnd(lastWordWasDataTable, stepModel.getExtendedDescription());
        }

    }

    @Override
    public void visitEnd(ScenarioCaseModel scenarioCase) {
        super.visitEnd(scenarioCase);
    }

    @Override
    public void visitEnd(ScenarioModel scenarioModel) {
        if (scenarioHasDataTable) {
            blockConverter.dataTable(new ScenarioDataTableImpl(scenarioModel));
        }
        blockConverter.scenarioEnd();
    }

    @Override
    public void visitEnd(ReportModel testCaseModel) {
        super.visitEnd(testCaseModel);
    }

}
