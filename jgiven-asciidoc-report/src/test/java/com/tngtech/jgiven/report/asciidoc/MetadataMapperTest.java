package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.StepStatus;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Maps metadata of scenarios and steps to the corresponding AsciiDoc representations.
 */
@RunWith(DataProviderRunner.class)
public class MetadataMapperTest {
    @Test
    @DataProvider({
        "SUCCESS, scenario-successful",
        "FAILED, scenario-failed",
        "SCENARIO_PENDING, scenario-pending",
        "SOME_STEPS_PENDING, scenario-pending"})
    public void toAsciiDocTagName(final ExecutionStatus executionStatus, final String expectedName) {
        // when
        final String startSnippet = MetadataMapper.toAsciiDocStartTag(executionStatus);
        final String endSnippet = MetadataMapper.toAsciiDocEndTag(executionStatus);

        // then
        assertThat(startSnippet).isEqualTo("// tag::" + expectedName + "[]");
        assertThat(endSnippet).isEqualTo("// end::" + expectedName + "[]");
    }

    @Test
    @DataProvider({
        "SUCCESS, icon:check-square[role=green]",
        "FAILED, icon:exclamation-circle[role=red]",
        "SCENARIO_PENDING, icon:ban[role=silver]",
        "SOME_STEPS_PENDING, icon:ban[role=silver]"})
    public void toHumanReadableExecutionStatus(final ExecutionStatus executionStatus, final String expectedStatus) {
        final String actualStatus = MetadataMapper.toHumanReadableStatus(executionStatus);

        assertThat(actualStatus).isEqualTo(expectedStatus);
    }

    @Test
    @DataProvider({
        "PASSED, icon:check-square[role=green]",
        "FAILED, icon:exclamation-circle[role=red]",
        "SKIPPED, icon:step-forward[role=silver]",
        "PENDING, icon:ban[role=silver]"})
    public void toHumanReadableStepStatus(final StepStatus stepStatus, final String expectedStatus) {
        final String actualStatus = MetadataMapper.toHumanReadableStatus(stepStatus);

        assertThat(actualStatus).isEqualTo(expectedStatus);
    }

    @Test
    public void toScenarioDurationBelow1ms() {
        final String actualDuration = MetadataMapper.toHumanReadableScenarioDuration(999_999);

        assertThat(actualDuration).isEqualTo("0ms");
    }

    @Test
    @DataProvider({
        "   1000000, 1ms",
        " 999999999, 999ms",
        "1000000000, 1s 0ms",
        "1000999999, 1s 0ms",
        "1001000000, 1s 1ms"})
    public void toScenarioDurationForDurationOver1ms(final long nanoseconds, final String expectedDuration) {
        final String actualDuration = MetadataMapper.toHumanReadableScenarioDuration(nanoseconds);

        assertThat(actualDuration).isEqualTo(expectedDuration);
    }

    @Test
    public void toStepDurationBelow10ms() {
        final String actualDuration = MetadataMapper.toHumanReadableStepDuration(9_999_999);

        assertThat(actualDuration).isEmpty();
    }

    @Test
    @DataProvider({
        "  10000000, (10ms)",
        " 999999999, (999ms)",
        "1000000000, (1s 0ms)",
        "1000999999, (1s 0ms)",
        "1001000000, (1s 1ms)"})
    public void toStepDurationForDurationOver1ms(final long nanoseconds, final String expectedDuration) {
        final String actualDuration = MetadataMapper.toHumanReadableStepDuration(nanoseconds);

        assertThat(actualDuration).isEqualTo(expectedDuration);
    }
}
