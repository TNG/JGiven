package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.ReportStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class AsciiDocFeatureBlockConverterTest {

    private final ReportBlockConverter converter = new AsciiDocReportBlockConverter();

    @Test
    public void convert_feature_header_without_description() {
        // given
        final ReportStatistics statistics = new ReportStatistics();
        statistics.numScenarios = 42;
        statistics.numFailedScenarios = 21;
        statistics.numPendingScenarios = 13;
        statistics.numSuccessfulScenarios = 8;

        // when
        final String block = converter.convertFeatureHeaderBlock("My first feature", statistics, null);

        // then
        assertThatBlockContainsLines(block,
            "=== My first feature",
            "",
            "icon:check-square[role=green] 8 Successful, icon:exclamation-circle[role=red] 21 Failed, "
                + "icon:ban[role=silver] 13 Pending, 42 Total (0ms)");
    }

    @Test
    public void convert_feature_header_with_description() {
        // given
        final ReportStatistics statistics = new ReportStatistics();
        statistics.numScenarios = 42;
        statistics.numFailedScenarios = 21;
        statistics.numPendingScenarios = 13;
        statistics.numSuccessfulScenarios = 8;

        // when
        final String block =
            converter.convertFeatureHeaderBlock("My first feature", statistics, "A very nice feature.");

        // then
        assertThatBlockContainsLines(block,
            "=== My first feature",
            "",
            "icon:check-square[role=green] 8 Successful, icon:exclamation-circle[role=red] 21 Failed, "
                + "icon:ban[role=silver] 13 Pending, 42 Total (0ms)",
            "",
            "+++A very nice feature.+++");
    }

    private static void assertThatBlockContainsLines(final String block, final String... expectedLines) {
        final String[] blockLines = block.split(System.lineSeparator());
        assertThat(blockLines).hasSize(expectedLines.length).containsExactly(expectedLines);
    }
}
