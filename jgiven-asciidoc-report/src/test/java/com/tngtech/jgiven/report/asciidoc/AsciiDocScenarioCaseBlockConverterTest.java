package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class AsciiDocScenarioCaseBlockConverterTest {

    public static final long ARBITRARY_DURATION = 42_000_000L;
    private static final String LINE_BREAK = System.lineSeparator();
    private final ReportBlockConverter converter = new AsciiDocBlockConverter();

    @ParameterizedTest
    @CsvSource({ "SUCCESS, check-square[role=green]",
        "FAILED, exclamation-circle[role=red]",
        "SCENARIO_PENDING, ban[role=silver]",
        "SOME_STEPS_PENDING, ban[role=silver]"})
    void convert_scenario_case_header_without_description(final ExecutionStatus executionStatus,
            final String icon) {
        // when
        var block = converter.convertCaseHeaderBlock(1, executionStatus, ARBITRARY_DURATION, null);

        // then
        assertThatBlockContainsLines(block,
            "===== Case 1",
            "",
            "icon:" + icon + " (42ms)");
    }

    @ParameterizedTest
    @CsvSource({ "SUCCESS, check-square[role=green]",
        "FAILED, exclamation-circle[role=red]",
        "SCENARIO_PENDING, ban[role=silver]",
        "SOME_STEPS_PENDING, ban[role=silver]"})
    void convert_scenario_case_header_with_empty_description(final ExecutionStatus executionStatus,
            final String icon) {
        // when
        var block = converter.convertCaseHeaderBlock(2, executionStatus, ARBITRARY_DURATION, "");

        // then
        assertThatBlockContainsLines(block,
                "===== Case 2",
                "",
                "icon:" + icon + " (42ms)");
    }

    @Test
    void convert_scenario_case_header_with_description() {
        // when
        var block = converter.convertCaseHeaderBlock(3, ExecutionStatus.SUCCESS, ARBITRARY_DURATION, "First case");

        // then
        assertThatBlockContainsLines(block,
            "===== Case 3: First case",
            "",
            "icon:check-square[role=green] (42ms)");
    }

    @Test
    void convert_scenario_case_footer_without_stacktrace() {
        // when
        final var block = converter.convertCaseFooterBlock("Something is broken" + LINE_BREAK + "inside me", null);

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
    void convert_scenario_case_footer_with_stacktrace() {
        // given
        final var stackTraceLines = List.of("broken line 1", "broken line 2");

        // when
        final var block =
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
        final var blockLines = block.split(System.lineSeparator());
        assertThat(blockLines).hasSize(expectedLines.length).containsExactly(expectedLines);
    }
}
