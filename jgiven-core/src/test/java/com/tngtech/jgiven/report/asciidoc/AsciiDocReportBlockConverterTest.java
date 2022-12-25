package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.ReportStatistics;
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
        final String block = converter.convertFeatureHeader(
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
        final String block = converter.convertFeatureHeader(
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
    public void sectionTitle() {
        // arrange

        // act
        final String block = converter.sectionTitle("first section of scenario");

        // assert
        Assertions.assertThat(block).isEqualTo(".first section of scenario");
    }
}