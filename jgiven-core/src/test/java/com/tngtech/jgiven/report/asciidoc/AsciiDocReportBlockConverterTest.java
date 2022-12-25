package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ReportStatistics;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class AsciiDocReportBlockConverterTest {

    private AsciiDocReportBlockConverter converter;


    @Before
    public void setUp() {
        converter = new AsciiDocReportBlockConverter(null);
    }

    @Test
    public void convert_feature_header_without_description() {
        // arrange
        final ReportStatistics statistics = new ReportStatistics();
        statistics.numScenarios = 42;
        statistics.numFailedScenarios = 21;
        statistics.numPendingScenarios = 13;
        statistics.numSuccessfulScenarios = 8;

        // act
        final String block = converter.convertFeatureHeaderBlock(
            "My first feature", statistics, null);

        // assert
        Assertions.assertThat(block).hasLineCount(3)
            .containsSequence(
                "=== My first feature\n",
                "\n",
                "8 Successful, 21 Failed, 13 Pending, 42 Total (0s 0ms)");
    }

    @Test
    public void convert_feature_header_with_description() {
        // arrange
        final ReportStatistics statistics = new ReportStatistics();
        statistics.numScenarios = 42;
        statistics.numFailedScenarios = 21;
        statistics.numPendingScenarios = 13;
        statistics.numSuccessfulScenarios = 8;

        // act
        final String block = converter.convertFeatureHeaderBlock(
            "My first feature", statistics, "A very nice feature.");

        // assert
        Assertions.assertThat(block).hasLineCount(5)
            .containsSequence(
                "=== My first feature\n",
                "\n",
                "8 Successful, 21 Failed, 13 Pending, 42 Total (0s 0ms)\n",
                "\n",
                "A very nice feature");
    }

    @Test
    public void convert_scenario_header_without_tags_or_description() {
        // arrange
        List<String> tagNames = new ArrayList<>();

        // act
        String block = converter.convertScenarioHeaderBlock("my first scenario", ExecutionStatus.FAILED,
                1000300000L, tagNames, null);

        // assert
        Assertions.assertThat(block).hasLineCount(3)
            .containsSequence(
                "=== My first scenario\n",
                "\n",
                "[FAILED] (1s 0ms)");
    }

    @Test
    public void convert_scenario_header_with_tags_and_no_description() {
        // arrange
        List<String> tagNames = new ArrayList<>();
        tagNames.add("Best Tag");

        // act
        String block = converter.convertScenarioHeaderBlock("my first scenario", ExecutionStatus.SCENARIO_PENDING,
            9000000L, tagNames, "");

        // assert
        Assertions.assertThat(block).hasLineCount(5)
            .containsSequence(
                "=== My first scenario\n",
                "\n",
                "[PENDING] (0s 9ms)\n",
                "\n",
                "Tags: _Best Tag_");
    }

    @Test
    public void convert_scenario_header_with_description_and_no_tags() {
        // arrange
        List<String> tagNames = new ArrayList<>();

        // act
        String block = converter.convertScenarioHeaderBlock("my first scenario", ExecutionStatus.SOME_STEPS_PENDING,
            2005000000L, tagNames, "Best scenario ever!!!");

        // assert
        Assertions.assertThat(block).hasLineCount(5)
            .containsSequence(
                "=== My first scenario\n",
                "\n",
                "[PENDING] (2s 5ms)\n",
                "\n",
                "Best scenario ever!!!");
    }

    @Test
    public void convert_scenario_header_with_tags_and_description() {
        // arrange
        List<String> tagNames = new ArrayList<>();
        tagNames.add("Best Tag");

        // act
        String block = converter.convertScenarioHeaderBlock("my first scenario", ExecutionStatus.SUCCESS,
            3000000000L, tagNames, "Best scenario ever!!!");

        // assert
        Assertions.assertThat(block).hasLineCount(7)
            .containsSequence(
                "=== My first scenario\n",
                "\n",
                "[SUCCESS] (3s 0ms)\n",
                "\n",
                "Best scenario ever!!!\n",
                "\n",
                "Tags: _Best Tag_");
    }

    @Test
    public void sectionTitle() {
        // arrange

        // act
        final String block = converter.sectionTitle("first section of scenario");

        // assert
        Assertions.assertThat(block).isEqualTo(".first section of scenario");
    }
}