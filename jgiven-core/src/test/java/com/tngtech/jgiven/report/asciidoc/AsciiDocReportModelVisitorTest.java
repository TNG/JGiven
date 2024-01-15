package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.CasesTable;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportStatistics;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.report.model.Tag;
import com.tngtech.jgiven.report.model.Word;
import java.util.List;
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
    public void visits_a_simple_report() {
        // given
        ReportModel report = mkReport(mkScenario("Simple Scenario", false, mkScenarioCase(
                mkStep("Given", "state"),
                mkStep("When", "action"),
                mkStep("Then", "outcome"))));

        // when
        report.accept(reportModelVisitor);

        // then
        assertThat(reportModelVisitor.getResult())
                .isEqualTo(ImmutableList.of(
                        "FeatureHeaderBlock",
                        "ScenarioHeaderBlock",
                        "FirstStepBlock",
                        "StepBlock",
                        "StepBlock",
                        "ScenarioFooterBlock"));
    }

    @Test
    public void visits_a_report_with_two_scenarios() {
        // given

        ReportModel report = mkReport(
                mkScenario("Scenario One", false, mkScenarioCase(
                        mkStep("Given", "state"),
                        mkStep("When", "action"),
                        mkStep("Then", "outcome"))),
                mkScenario("Scenario Two", false, mkScenarioCase(
                        mkStep("Given", "state"),
                        mkStep("When", "action"),
                        mkStep("Then", "outcome"))));

        // when
        report.accept(reportModelVisitor);

        // then
        assertThat(reportModelVisitor.getResult())
                .isEqualTo(ImmutableList.of(
                        "FeatureHeaderBlock",
                        "ScenarioHeaderBlock",
                        "FirstStepBlock",
                        "StepBlock",
                        "StepBlock",
                        "ScenarioFooterBlock",
                        "ScenarioHeaderBlock",
                        "FirstStepBlock",
                        "StepBlock",
                        "StepBlock",
                        "ScenarioFooterBlock"));
    }

    @Test
    public void visits_a_scenario_with_two_standalone_cases() {
        // given
        ReportModel report = mkReport(mkScenario("Simple Scenario", false,
                mkScenarioCase(
                        mkStep("Given", "state"),
                        mkStep("When", "action"),
                        mkStep("Then", "outcome")),
                mkScenarioCase(
                        mkStep("Given", "state"),
                        mkStep("When", "action"),
                        mkStep("Then", "outcome"))));

        // when
        report.accept(reportModelVisitor);

        // then
        assertThat(reportModelVisitor.getResult())
                .isEqualTo(ImmutableList.of(
                        "FeatureHeaderBlock",
                        "ScenarioHeaderBlock",
                        "CaseHeaderBlock",
                        "FirstStepBlock",
                        "StepBlock",
                        "StepBlock",
                        "CaseHeaderBlock",
                        "FirstStepBlock",
                        "StepBlock",
                        "StepBlock",
                        "ScenarioFooterBlock"));
    }

    @Test
    public void visits_a_scenario_with_two_cases_as_table() {
        // given
        ReportModel report = mkReport(mkScenario("Simple Scenario", true,
                mkScenarioCase(
                        mkStep("Given", "state"),
                        mkStep("When", "action"),
                        mkStep("Then", "outcome")),
                mkScenarioCase(
                        mkStep("Given", "state"),
                        mkStep("When", "action"),
                        mkStep("Then", "outcome"))));

        // when
        report.accept(reportModelVisitor);

        // then
        assertThat(reportModelVisitor.getResult())
                .isEqualTo(ImmutableList.of(
                        "FeatureHeaderBlock",
                        "ScenarioHeaderBlock",
                        "FirstStepBlock",
                        "StepBlock",
                        "StepBlock",
                        "CasesTableBlock",
                        "ScenarioFooterBlock"));
    }

    @Test
    public void visits_a_scenario_with_a_section() {
        // given
        ReportModel report = mkReport(mkScenario("Simple Scenario", false, mkScenarioCase(
                mkSectionTitle("Some Section"),
                mkStep("Given", "state"),
                mkStep("When", "action"),
                mkStep("Then", "outcome"))));

        // when
        report.accept(reportModelVisitor);

        // then
        assertThat(reportModelVisitor.getResult())
                .isEqualTo(ImmutableList.of(
                        "FeatureHeaderBlock",
                        "ScenarioHeaderBlock",
                        "FirstStepBlock",
                        "StepBlock",
                        "StepBlock",
                        "ScenarioFooterBlock"));
    }

    @Test
    public void visits_a_scenario_with_two_sections() {
        // given
        ReportModel report = mkReport(mkScenario("Simple Scenario", false, mkScenarioCase(
                mkSectionTitle("First Section"),
                mkStep("Given", "state"),
                mkStep("When", "action"),
                mkStep("Then", "outcome"),
                mkSectionTitle("Second Section"),
                mkStep("Given", "other state"),
                mkStep("When", "other action"),
                mkStep("Then", "other outcome"))));

        // when
        report.accept(reportModelVisitor);

        // then
        assertThat(reportModelVisitor.getResult())
                .isEqualTo(ImmutableList.of(
                        "FeatureHeaderBlock",
                        "ScenarioHeaderBlock",
                        "FirstStepBlock",
                        "StepBlock",
                        "StepBlock",
                        "FirstStepBlock",
                        "StepBlock",
                        "StepBlock",
                        "ScenarioFooterBlock"));
    }

    private static ReportModel mkReport(final ScenarioModel... scenarios) {
        ReportModel report = new ReportModel();
        for (final ScenarioModel scenarioModel : scenarios) {
            report.addScenarioModel(scenarioModel);

        }
        return report;
    }

    private static ScenarioModel mkScenario(final String description, final boolean casesAsTable,
                                            final ScenarioCaseModel... cases) {
        ScenarioModel scenario = new ScenarioModel();
        scenario.setDescription(description);
        scenario.setCasesAsTable(casesAsTable);
        for (final ScenarioCaseModel caseModel : cases) {
            scenario.addCase(caseModel);
        }
        return scenario;
    }

    private static ScenarioCaseModel mkScenarioCase(final StepModel... steps) {
        ScenarioCaseModel scenarioCase = new ScenarioCaseModel();
        for (final StepModel step : steps) {
            scenarioCase.addStep(step);
        }
        return scenarioCase;
    }

    private static StepModel mkSectionTitle(final String title) {
        final Word sectionWord = new Word(title);
        final StepModel stepModel = new StepModel(title, List.of(sectionWord));
        stepModel.setIsSectionTitle(true);
        return stepModel;
    }

    private static StepModel mkStep(final String introWord, final String object) {
        StepModel step = new StepModel();
        step.addIntroWord(Word.introWord(introWord));
        step.addWords(new Word("some"), new Word(object));
        return step;
    }

    private static class MyFakeReportBlockConverter implements ReportBlockConverter {

        @Override
        public String convertStatisticsBlock(final ListMultimap<String, ReportStatistics> featureStatistics,
                final ReportStatistics totalStatistics) {
            return "StatisticsBlock";
        }

        @Override
        public String convertFeatureHeaderBlock(String featureName, ReportStatistics statistics,
                String description) {
            return "FeatureHeaderBlock";
        }

        @Override
        public String convertScenarioHeaderBlock(String name, ExecutionStatus executionStatus, long duration,
                                                 List<Tag> tags, String extendedDescription) {
            return "ScenarioHeaderBlock";
        }

        @Override
        public String convertCaseHeaderBlock(final int caseNr, final ExecutionStatus executionStatus,
                                             final long duration, final String description) {
            return "CaseHeaderBlock";
        }

        @Override
        public String convertFirstStepBlock(final int depth, final List<Word> words, final StepStatus status,
                final long durationInNanos, final String extendedDescription,
                final boolean caseIsUnsuccessful, final String currentSectionTitle) {
            return "FirstStepBlock";
        }

        @Override
        public String convertStepBlock(int depth, List<Word> words, StepStatus status, long durationInNanos,
                String extendedDescription, boolean caseIsUnsuccessful) {
            return "StepBlock";
        }

        @Override
        public String convertCasesTableBlock(CasesTable casesTable) {
            return "CasesTableBlock";
        }

        @Override
        public String convertCaseFooterBlock(final String errorMessage, final List<String> stackTraceLines) {
            return "CaseFooterBlock";
        }

        @Override
        public String convertScenarioFooterBlock(ExecutionStatus executionStatus, final List<Tag> tags) {
            return "ScenarioFooterBlock";
        }
    }
}
