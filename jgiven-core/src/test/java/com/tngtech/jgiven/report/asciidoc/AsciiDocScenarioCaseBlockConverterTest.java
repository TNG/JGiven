package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class AsciiDocScenarioCaseBlockConverterTest {

    public static final long ARBIRTRARY_DURATION = 42_000_000L;
    private final ReportBlockConverter converter = new AsciiDocBlockConverter();

    @Test
    @DataProvider({"SUCCESS, check-square[role=green]",
        "FAILED, exclamation-circle[role=red]",
        "SCENARIO_PENDING, ban[role=silver]",
        "SOME_STEPS_PENDING, ban[role=silver]"})
    public void convert_scenario_case_header_without_description(final ExecutionStatus executionStatus,
            final String icon) {
        // when
        String block = converter.convertCaseHeaderBlock(1, executionStatus, ARBIRTRARY_DURATION, null);

        // then
        assertThatBlockContainsLines(block,
            "===== Case 1",
            "",
            "icon:" + icon + " (42ms)");
    }

    @Test
    @DataProvider({"SUCCESS, check-square[role=green]",
        "FAILED, exclamation-circle[role=red]",
        "SCENARIO_PENDING, ban[role=silver]",
        "SOME_STEPS_PENDING, ban[role=silver]"})
    public void convert_scenario_case_header_with_empty_description(final ExecutionStatus executionStatus,
            final String icon) {
        // when
        String block = converter.convertCaseHeaderBlock(2, executionStatus, ARBIRTRARY_DURATION, "");

        // then
        assertThatBlockContainsLines(block,
                "===== Case 2",
                "",
                "icon:" + icon + " (42ms)");
    }

    @Test
    public void convert_scenario_case_header_with_description() {
        // when
        String block = converter.convertCaseHeaderBlock(3, ExecutionStatus.SUCCESS, ARBIRTRARY_DURATION, "First case");

        // then
        assertThatBlockContainsLines(block,
            "===== Case 3: First case",
            "",
            "icon:check-square[role=green] (42ms)");
    }

    @Test
    public void convert_scenario_case_footer_without_stacktrace() {
        // when
        final String block = converter.convertCaseFooterBlock("Something is broken\ninside me", null);

        // then
        assertThatBlockContainsLines(block,
                "[.jg-exception]",
                "====",
                "[%hardbreaks]",
                "Something is broken",
                "inside me",
                "",
                "No stacktrace provided",
                "====");
    }

    @Test
    public void convert_scenario_case_footer_with_stacktrace() {
        // given
        final ImmutableList<String> stackTraceLines = ImmutableList.of("broken line 1", "broken line 2");

        // when
        final String block =
                converter.convertCaseFooterBlock("Something is broken", stackTraceLines);

        // then
        assertThatBlockContainsLines(block,
                "[.jg-exception]",
                "====",
                "[%hardbreaks]",
                "Something is broken",
                "",
                ".Show stacktrace",
                "[%collapsible]",
                "=====",
                "....",
                "broken line 1",
                "broken line 2",
                "....",
                "=====",
                "====");
    }

    private static void assertThatBlockContainsLines(final String block, final String... expectedLines) {
        final String[] blockLines = block.split(System.lineSeparator());
        assertThat(blockLines).hasSize(expectedLines.length).containsExactly(expectedLines);
    }
}
