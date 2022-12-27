package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.*;

import java.util.*;
import java.util.stream.Collectors;

class AsciiDocReportModelVisitor extends ReportModelVisitor {

    private final ReportBlockConverter blockConverter;
    private final List<String> asciiDocBlocks;
    private final ReportStatistics featureStatistics;
    private Map<String, Tag> featureTagMap;
    private boolean scenarioHasDataTable;
    private List<String> explicitScenarioParameters;
    private boolean skipCurrentCase;
    private boolean caseIsUnsuccessful;
    private String currentSectionTitle;

    public AsciiDocReportModelVisitor(ReportBlockConverter blockConverter, ReportStatistics featureStatistics) {
        this.blockConverter = blockConverter;
        this.asciiDocBlocks = new ArrayList<>();
        this.featureStatistics = featureStatistics;
    }

    @Override
    public void visit(ReportModel reportModel) {
        String featureName = Optional.ofNullable(reportModel.getName())
            .filter(name -> !name.isEmpty())
            .orElse(reportModel.getClassName());

        asciiDocBlocks.add(
            blockConverter.convertFeatureHeaderBlock(featureName, featureStatistics, reportModel.getDescription()));

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
            currentSectionTitle = stepModel.getName();
            return;
        }

        asciiDocBlocks.add(blockConverter.convertStepBlock(
            stepModel.getDepth(), stepModel.getWords(), stepModel.getStatus(), stepModel.getDurationInNanos(),
            stepModel.getExtendedDescription(), this.caseIsUnsuccessful, currentSectionTitle, scenarioHasDataTable));

        // clear section title after first step
        currentSectionTitle = null;
    }

    @Override
    public void visitEnd(ScenarioModel scenarioModel) {
        if (scenarioHasDataTable) {
            ScenarioDataTableImpl scenarioDataTable = new ScenarioDataTableImpl(scenarioModel);
            blockConverter.dataTable(scenarioDataTable);
        }
    }

    public List<String> getResult() {
        return Collections.unmodifiableList(asciiDocBlocks);
    }
}
