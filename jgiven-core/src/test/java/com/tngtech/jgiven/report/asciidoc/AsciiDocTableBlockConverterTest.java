package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.report.CasesTable;
import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ReportStatistics;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class AsciiDocTableBlockConverterTest {

    private final ReportBlockConverter converter = new AsciiDocReportBlockConverter();

    @Test
    public void convert_cases_table_without_descriptions() {
        // given
        ScenarioCaseModel case1 = new ScenarioCaseModel();
        case1.setCaseNr(1);
        case1.setStatus(ExecutionStatus.SUCCESS);
        case1.setDerivedArguments(ImmutableList.of("1", "2"));

        ScenarioCaseModel case2 = new ScenarioCaseModel();
        case2.setCaseNr(2);
        case2.setStatus(ExecutionStatus.FAILED);
        case2.setDerivedArguments(ImmutableList.of("3", "4"));

        ScenarioModel scenario = new ScenarioModel();
        scenario.addCase(case1);
        scenario.addCase(case2);
        scenario.addDerivedParameter("foo");
        scenario.addDerivedParameter("bar");

        CasesTable casesTable = new CasesTable(scenario);

        // when
        String block = converter.convertCasesTableBlock(casesTable);

        // then
        assertThatBlockContainsLines(block,
            ".Cases",
            "[.jg-casesTable%header,cols=\"h,1,1,>1\"]",
            "|===",
            "| # | foo | bar | Status",
            "| 1 | +1+ | +2+ | icon:check-square[role=green]",
            "| 2 | +3+ | +4+ | icon:exclamation-circle[role=red]",
            "|===");
    }

    @Test
    public void convert_cases_table_with_descriptions() {
        // given
        ScenarioCaseModel case1 = new ScenarioCaseModel();
        case1.setCaseNr(1);
        case1.setStatus(ExecutionStatus.SUCCESS);
        case1.setDerivedArguments(ImmutableList.of("1", "2"));
        case1.setDescription("First case");

        ScenarioCaseModel case2 = new ScenarioCaseModel();
        case2.setCaseNr(2);
        case2.setStatus(ExecutionStatus.FAILED);
        case2.setDerivedArguments(ImmutableList.of("3", "4"));
        case2.setDescription("Second case");

        ScenarioModel scenario = new ScenarioModel();
        scenario.addCase(case1);
        scenario.addCase(case2);
        scenario.addDerivedParameter("foo");
        scenario.addDerivedParameter("bar");

        CasesTable casesTable = new CasesTable(scenario);

        // when
        String block = converter.convertCasesTableBlock(casesTable);

        // then
        assertThatBlockContainsLines(block,
            ".Cases",
            "[.jg-casesTable%header,cols=\"h,1,1,1,>1\"]",
            "|===",
            "| # | Description | foo | bar | Status",
            "| 1 | +First case+ | +1+ | +2+ | icon:check-square[role=green]",
            "| 2 | +Second case+ | +3+ | +4+ | icon:exclamation-circle[role=red]",
            "|===");
    }


    @Test
    public void convert_cases_table_with_errors() {
        // given
        ScenarioCaseModel case1 = new ScenarioCaseModel();
        case1.setCaseNr(1);
        case1.setStatus(ExecutionStatus.FAILED);
        case1.setDerivedArguments(ImmutableList.of("1", "2"));
        case1.setErrorMessage("java.lang.AssertionError:\nvalue 5 is not 12");
        List<String> stackTrace = new ArrayList<>();
        stackTrace.add("exception in line 1");
        stackTrace.add("called in line 2");
        case1.setStackTrace(stackTrace);

        ScenarioCaseModel case2 = new ScenarioCaseModel();
        case2.setCaseNr(2);
        case2.setStatus(ExecutionStatus.FAILED);
        case2.setDerivedArguments(ImmutableList.of("3", "4"));

        ScenarioModel scenario = new ScenarioModel();
        scenario.addCase(case1);
        scenario.addCase(case2);
        scenario.addDerivedParameter("foo");
        scenario.addDerivedParameter("bar");

        CasesTable casesTable = new CasesTable(scenario);

        // when
        String block = converter.convertCasesTableBlock(casesTable);

        // then
        assertThatBlockContainsLines(block,
            ".Cases",
            "[.jg-casesTable%header,cols=\"h,1,1,>1\"]",
            "|===",
            "| # | foo | bar | Status",
            ".2+| 1 | +1+ | +2+ | icon:exclamation-circle[role=red]",
            "3+a|",
            "[.jg-exception]",
            "====",
            "[%hardbreaks]",
            "java.lang.AssertionError:",
            "value 5 is not 12",
            "",
            ".Show stacktrace",
            "[%collapsible]",
            "=====",
            "....",
            "exception in line 1",
            "called in line 2",
            "....",
            "=====",
            "====",
            "| 2 | +3+ | +4+ | icon:exclamation-circle[role=red]",
            "|===");
    }


    @Test
    public void convert_statistics() {
        // given
        final ReportStatistics statisticsOne = new ReportStatistics();
        statisticsOne.numClasses = 1;
        statisticsOne.numScenarios = 3;
        statisticsOne.numSuccessfulScenarios = 2;
        statisticsOne.numFailedScenarios = 1;
        statisticsOne.numCases = 3;
        statisticsOne.numFailedCases = 1;
        statisticsOne.numSteps = 13;

        final ReportStatistics statisticsTwo = new ReportStatistics();
        statisticsTwo.numClasses = 1;
        statisticsTwo.numScenarios = 2;
        statisticsTwo.numSuccessfulScenarios = 1;
        statisticsTwo.numPendingScenarios = 1;
        statisticsTwo.numCases = 2;
        statisticsTwo.numFailedCases = 1;
        statisticsTwo.numSteps = 8;

        final ReportStatistics totalStatistics = new ReportStatistics();
        totalStatistics.numClasses = 2;
        totalStatistics.numScenarios = 5;
        totalStatistics.numSuccessfulScenarios = 3;
        totalStatistics.numPendingScenarios = 1;
        totalStatistics.numFailedScenarios = 1;
        totalStatistics.numCases = 5;
        totalStatistics.numFailedCases = 1;
        totalStatistics.numSteps = 21;

        final ImmutableMap<String, ReportStatistics> featureStatistics =
            ImmutableMap.of("Feature One", statisticsOne, "Feature Two", statisticsTwo);

        // when
        final String block = converter.convertStatisticsBlock(featureStatistics, totalStatistics);

        // then
        assertThatBlockContainsLines(block,
            ".Total Statistics",
            "[options=\"header,footer\"]",
            "|===",
            "| feature | total classes | successful scenarios | failed scenarios | pending scenarios | "
                + "total scenarios | failed cases | total cases | total steps | duration",
            "| Feature One | 1 | 2 | 1 | 0 | 3 | 1 | 3 | 13 | 0ms",
            "| Feature Two | 1 | 1 | 0 | 1 | 2 | 1 | 2 | 8 | 0ms",
            "| sum | 2 | 3 | 1 | 1 | 5 | 1 | 5 | 21 | 0ms",
            "|===");
    }

    private static void assertThatBlockContainsLines(final String block, final String... expectedLines) {
        final String[] blockLines = block.split(System.lineSeparator());
        assertThat(blockLines).hasSize(expectedLines.length).containsExactly(expectedLines);
    }
}
