package com.tngtech.jgiven.report.asciidoc;

import com.google.common.collect.ImmutableList;
import com.tngtech.jgiven.report.CasesTable;
import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportStatistics;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.report.model.Word;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class AsciiDocReportModelVisitorTest {
    private final ReportBlockConverter blockConverter = new MyFakeReportBlockConverter();

    private AsciiDocReportModelVisitor reportModelVisitor;

    @Before
    public void setUp() {
        ReportStatistics reportStatistics = new ReportStatistics();
        reportModelVisitor = new AsciiDocReportModelVisitor(blockConverter, reportStatistics);
    }

    @Test
    public void visitTheReportModel() {
        // arrange
        StepModel stepModel = new StepModel();
        stepModel.addIntroWord(Word.introWord("Given"));
        stepModel.addWords(new Word("some"), new Word("state"));

        ScenarioCaseModel scenarioCaseOne = new ScenarioCaseModel();
        scenarioCaseOne.addStep(stepModel);

        ScenarioModel currentScenarioModel = new ScenarioModel();
        currentScenarioModel.addCase(scenarioCaseOne);
        currentScenarioModel.addCase(new ScenarioCaseModel());

        ReportModel reportModel = new ReportModel();
        reportModel.addScenarioModel(currentScenarioModel);

        // act
        reportModel.accept(reportModelVisitor);

        // assess
        Assertions.assertThat(reportModelVisitor.getResult())
            .isEqualTo(ImmutableList.of(
                "convertFeatureHeaderBlock",
                "convertScenarioHeaderBlock",
                "convertCaseHeaderBlock",
                "convertFirstStepBlock",
                "convertCaseHeaderBlock",
                "convertScenarioFooterBlock"));
    }


    @Test
    public void visitTheReportModelWithTwoSections() {
        // arrange
        final StepModel sectionOne = new StepModel();
        sectionOne.setIsSectionTitle(true);
        sectionOne.addWords(new Word("First Section"));
        StepModel givenStep = new StepModel();
        givenStep.addIntroWord(Word.introWord("Given"));
        givenStep.addWords(new Word("some"), new Word("state"));

        final StepModel sectionTwo = new StepModel();
        sectionTwo.setIsSectionTitle(true);
        sectionTwo.addWords(new Word("Second Section"));
        StepModel whenStep = new StepModel();
        whenStep.addIntroWord(Word.introWord("When"));
        whenStep.addWords(new Word("some"), new Word("action"));

        ScenarioCaseModel scenarioCaseOne = new ScenarioCaseModel();
        scenarioCaseOne.addStep(sectionOne);
        scenarioCaseOne.addStep(givenStep);
        scenarioCaseOne.addStep(givenStep);
        scenarioCaseOne.addStep(sectionTwo);
        scenarioCaseOne.addStep(whenStep);

        ScenarioModel currentScenarioModel = new ScenarioModel();
        currentScenarioModel.addCase(scenarioCaseOne);
        currentScenarioModel.addCase(new ScenarioCaseModel());

        ReportModel reportModel = new ReportModel();
        reportModel.addScenarioModel(currentScenarioModel);

        // act
        reportModel.accept(reportModelVisitor);

        // assess
        Assertions.assertThat(reportModelVisitor.getResult())
            .isEqualTo(ImmutableList.of(
                "convertFeatureHeaderBlock",
                "convertScenarioHeaderBlock",
                "convertCaseHeaderBlock",
                "convertFirstStepBlock",
                "convertStepBlock",
                "convertFirstStepBlock",
                "convertCaseHeaderBlock",
                "convertScenarioFooterBlock"));
    }

    private static class MyFakeReportBlockConverter implements ReportBlockConverter {
        @Override
        public String convertStatisticsBlock(final Map<String, ReportStatistics> featureStatistics,
                                             final ReportStatistics totalStatistics) {
            return "convertStatisticsBlock";
        }

        @Override
        public String convertFeatureHeaderBlock(String featureName, ReportStatistics statistics,
                                                String description) {
            return "convertFeatureHeaderBlock";
        }

        @Override
        public String convertScenarioHeaderBlock(String name, ExecutionStatus executionStatus, long duration,
                                                 List<String> tagNames, String extendedDescription) {
            return "convertScenarioHeaderBlock";
        }

        @Override
        public String convertCaseHeaderBlock(int caseNr, final ExecutionStatus executionStatus,
                                             String description) {
            return "convertCaseHeaderBlock";
        }

        @Override
        public String convertFirstStepBlock(final int depth, final List<Word> words, final StepStatus status,
                                            final long durationInNanos, final String extendedDescription,
                                            final boolean caseIsUnsuccessful, final String currentSectionTitle) {
            return "convertFirstStepBlock";
        }

        @Override
        public String convertStepBlock(int depth, List<Word> words, StepStatus status, long durationInNanos,
                                       String extendedDescription, boolean caseIsUnsuccessful) {
            return "convertStepBlock";
        }

        @Override
        public String convertCasesTableBlock(CasesTable casesTable) {
            return "convertCasesTableBlock";
        }

        @Override
        public String convertCaseFooterBlock(final String errorMessage, final List<String> stackTraceLines) {
            return "convertCaseFooterBlock";
        }

        @Override
        public String convertScenarioFooterBlock(ExecutionStatus executionStatus) {
            return "convertScenarioFooterBlock";
        }
    }
}
