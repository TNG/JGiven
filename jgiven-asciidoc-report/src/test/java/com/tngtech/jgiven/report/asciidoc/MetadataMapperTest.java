package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.StepStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Maps metadata of scenarios and steps to the corresponding AsciiDoc representations.
 */
class MetadataMapperTest {
    @ParameterizedTest
    @CsvSource({
            "SUCCESS, status-is-successful",
            "SCENARIO_PENDING, status-is-pending",
            "SOME_STEPS_PENDING, status-is-pending",
            "ABORTED, status-is-aborted",
            "FAILED, status-is-failed",
    })
    void toAsciiDocTagName(final ExecutionStatus executionStatus, final String expectedName) {
        // when
        final var startSnippet = MetadataMapper.toAsciiDocStartTag(executionStatus);
        final var endSnippet = MetadataMapper.toAsciiDocEndTag(executionStatus);

        // then
        assertThat(startSnippet).isEqualTo("// tag::" + expectedName + "[]");
        assertThat(endSnippet).isEqualTo("// end::" + expectedName + "[]");
    }

    @ParameterizedTest
    @CsvSource({
            "SUCCESS, icon:check-square[role=green]",
            "FAILED, icon:exclamation-circle[role=red]",
            "SCENARIO_PENDING, icon:ban[role=silver]",
            "SOME_STEPS_PENDING, icon:ban[role=silver]"})
    void toHumanReadableExecutionStatus(final ExecutionStatus executionStatus, final String expectedStatus) {
        final var actualStatus = MetadataMapper.toHumanReadableStatus(executionStatus);

        assertThat(actualStatus).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @CsvSource({
            "PASSED, icon:check-square[role=green]",
            "FAILED, icon:exclamation-circle[role=red]",
            "SKIPPED, icon:step-forward[role=silver]",
            "PENDING, icon:ban[role=silver]"})
    void toHumanReadableStepStatus(final StepStatus stepStatus, final String expectedStatus) {
        final var actualStatus = MetadataMapper.toHumanReadableStatus(stepStatus);

        assertThat(actualStatus).isEqualTo(expectedStatus);
    }

    @Test
    void toScenarioDurationBelow1ms() {
        final var actualDuration = MetadataMapper.toHumanReadableScenarioDuration(999_999);

        assertThat(actualDuration).isEqualTo("0ms");
    }

    @ParameterizedTest
    @CsvSource({
            "   1000000, 1ms",
            " 999999999, 999ms",
            "1000000000, 1s 0ms",
            "1000999999, 1s 0ms",
            "1001000000, 1s 1ms"})
    void toScenarioDurationForDurationOver1ms(final long nanoseconds, final String expectedDuration) {
        final var actualDuration = MetadataMapper.toHumanReadableScenarioDuration(nanoseconds);

        assertThat(actualDuration).isEqualTo(expectedDuration);
    }

    @Test
    void toStepDurationBelow10ms() {
        final var actualDuration = MetadataMapper.toHumanReadableStepDuration(9_999_999);

        assertThat(actualDuration).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "  10000000, (10ms)",
            " 999999999, (999ms)",
            "1000000000, (1s 0ms)",
            "1000999999, (1s 0ms)",
            "1001000000, (1s 1ms)"})
    void toStepDurationForDurationOver1ms(final long nanoseconds, final String expectedDuration) {
        final var actualDuration = MetadataMapper.toHumanReadableStepDuration(nanoseconds);

        assertThat(actualDuration).isEqualTo(expectedDuration);
    }
}
