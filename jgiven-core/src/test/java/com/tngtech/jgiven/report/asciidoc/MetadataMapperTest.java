package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.StepStatus;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Maps metadata of scenarios and steps to the corresponding AsciiDoc representations.
 */
@RunWith(DataProviderRunner.class)
public class MetadataMapperTest {

    @Test
    public void toAsciiDocTagStart() {
        final String actualName = MetadataMapper.toAsciiDocTagStart(ExecutionStatus.SUCCESS);

        assertThat(actualName).isEqualTo("// tag::scenario-successful[]");
    }

    @Test
    public void toAsciiDocTagEnd() {
        final String actualName = MetadataMapper.toAsciiDocTagEnd(ExecutionStatus.SUCCESS);

        assertThat(actualName).isEqualTo("// end::scenario-successful[]");
    }

    @Test
    @DataProvider({
        "SUCCESS, scenario-successful",
        "FAILED, scenario-failed",
        "SCENARIO_PENDING, scenario-pending",
        "SOME_STEPS_PENDING, scenario-pending"})
    public void toAsciiDocTagName(final ExecutionStatus executionStatus, final String expectedName) {
        final String actualName = MetadataMapper.toAsciiDocTagName(executionStatus);

        assertThat(actualName).isEqualTo(expectedName);
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
    public void toHumanReadableDurationForDurationBelow1ms() {
        final Optional<String> actualDuration = MetadataMapper.toHumanReadableDuration(999999);

        assertThat(actualDuration).isEmpty();
    }

    @Test
    @DataProvider({
        "1000000, 1ms",
        "999999999, 999ms",
        "1000000000, 1s 0ms",
        "1000999999, 1s 0ms",
        "1001000000, 1s 1ms"})
    public void toHumanReadableDurationForDurationOver1ms(final int nanoseconds, final String expectedDuration) {
        final Optional<String> actualDuration = MetadataMapper.toHumanReadableDuration(nanoseconds);

        assertThat(actualDuration).hasValue(expectedDuration);
    }
}
