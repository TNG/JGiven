package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.CasesTable;
import com.tngtech.jgiven.report.model.CasesTableCalculator;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelVisitor;
import com.tngtech.jgiven.report.model.ReportStatistics;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Tag;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class AsciiDocReportModelVisitor extends ReportModelVisitor {

    private final ReportBlockConverter blockConverter;
    private final ReportStatistics featureStatistics;
    private final CasesTableCalculator tableCalculator;
    private final List<String> asciiDocBlocks;
    private final Map<String, Integer> featureTagIds;
    private Map<String, Tag> featureTagMap;
    private List<Tag> tagList;
    private boolean scenarioHasDataTable;
    private boolean scenarioHasMultipleCases;
    private boolean skipCurrentCase;
    private boolean caseIsUnsuccessful;
    private boolean isFirstStepInCase;
    private String currentSectionTitle;

    public AsciiDocReportModelVisitor(final ReportBlockConverter blockConverter,
                                      final ReportStatistics featureStatistics) {
        this.blockConverter = blockConverter;
        this.featureStatistics = featureStatistics;
        this.tableCalculator = new CasesTableCalculator();
        this.asciiDocBlocks = new ArrayList<>();
        this.featureTagIds = new HashMap<>();
    }

    @Override
    public void visit(final ReportModel reportModel) {
        String featureName = Optional.ofNullable(reportModel.getName())
                .filter(name -> !name.isEmpty())
                .orElse(reportModel.getClassName());

        String featureHeader =
                blockConverter.convertFeatureHeaderBlock(featureName, featureStatistics, reportModel.getDescription());
        asciiDocBlocks.add(featureHeader);

        featureTagMap = reportModel.getTagMap();
    }

    @Override
    public void visit(final ScenarioModel scenarioModel) {
        scenarioModel.getTagIds().forEach(tagId -> featureTagIds.merge(tagId, 1, Integer::sum));

        tagList = scenarioModel.getTagIds().stream()
                .map(this.featureTagMap::get)
                .collect(Collectors.toList());

        String scenarioHeader = blockConverter.convertScenarioHeaderBlock(scenarioModel.getDescription(),
                scenarioModel.getExecutionStatus(), scenarioModel.getDurationInNanos(), tagList,
                scenarioModel.getExtendedDescription());
        asciiDocBlocks.add(scenarioHeader);

        scenarioHasDataTable = scenarioModel.isCasesAsTable();
        scenarioHasMultipleCases = scenarioModel.getScenarioCases().size() > 1;
    }

    @Override
    public void visit(final ScenarioCaseModel scenarioCase) {
        skipCurrentCase = scenarioHasDataTable && scenarioCase.getCaseNr() > 1;
        if (skipCurrentCase) {
            return;
        }

        if (scenarioHasMultipleCases && !scenarioHasDataTable) {
            String caseHeader = blockConverter.convertCaseHeaderBlock(scenarioCase.getCaseNr(),
                    scenarioCase.getExecutionStatus(), scenarioCase.getDurationInNanos(),
                    scenarioCase.getDescription());
            asciiDocBlocks.add(caseHeader);
        }

        caseIsUnsuccessful = scenarioCase.getExecutionStatus() != ExecutionStatus.SUCCESS;
        isFirstStepInCase = true;
    }

    @Override
    public void visit(final StepModel stepModel) {
        if (skipCurrentCase) {
            return;
        }

        if (Boolean.TRUE.equals(stepModel.isSectionTitle())) {
            currentSectionTitle = stepModel.getName();
            isFirstStepInCase = true;
            return;
        }

        String stepBlock;
        if (isFirstStepInCase) {
            stepBlock = blockConverter.convertFirstStepBlock(
                    stepModel.getDepth(), stepModel.getWords(), stepModel.getStatus(), stepModel.getDurationInNanos(),
                    stepModel.getExtendedDescription(), this.caseIsUnsuccessful, currentSectionTitle);
        } else {
            stepBlock = blockConverter.convertStepBlock(
                    stepModel.getDepth(), stepModel.getWords(), stepModel.getStatus(), stepModel.getDurationInNanos(),
                    stepModel.getExtendedDescription(), this.caseIsUnsuccessful);
        }
        asciiDocBlocks.add(stepBlock);

        // clear section title after first step in section
        currentSectionTitle = null;
        isFirstStepInCase = false;
    }

    @Override
    public void visitEnd(final ScenarioCaseModel scenarioCase) {
        final String errorMessage = scenarioCase.getErrorMessage();
        if (!scenarioHasDataTable && errorMessage != null) {
            asciiDocBlocks.add(blockConverter.convertCaseFooterBlock(errorMessage, scenarioCase.getStackTrace()));
        }
    }

    @Override
    public void visitEnd(final ScenarioModel scenarioModel) {
        if (scenarioHasDataTable) {
            final CasesTable casesTable = tableCalculator.collectCases(scenarioModel);
            final String casesTableBlock = blockConverter.convertCasesTableBlock(casesTable);
            asciiDocBlocks.add(casesTableBlock);
        }

        String scenarioFooter = blockConverter.convertScenarioFooterBlock(scenarioModel.getExecutionStatus(), tagList);
        asciiDocBlocks.add(scenarioFooter);
    }

    public List<String> getResult() {
        return Collections.unmodifiableList(asciiDocBlocks);
    }


    public Map<String, Integer> getUsedTags() {
        return Collections.unmodifiableMap(featureTagIds);
    }

}
