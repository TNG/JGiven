package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.StepStatus;
import java.time.Duration;

final class MetadataMapper {
    private static final String ICON_CHECK_MARK = "icon:check-square[role=green]";
    private static final String ICON_EXCLAMATION_MARK = "icon:exclamation-circle[role=red]";
    private static final String ICON_BANNED = "icon:ban[role=silver]";
    private static final String ICON_STEP_FORWARD = "icon:step-forward[role=silver]";
    private static final int NANOSECONDS_PER_MILLISECOND = 1000000;

    private MetadataMapper() {
        // static helper class isn't intended to be instantiated
    }

    static String toAsciiDocStartTag(ExecutionStatus executionStatus) {
        return "// tag::" + toAsciiDocTagName(executionStatus) + "[]";
    }

    static String toAsciiDocEndTag(ExecutionStatus executionStatus) {
        return "// end::" + toAsciiDocTagName(executionStatus) + "[]";
    }

    private static String toAsciiDocTagName(final ExecutionStatus executionStatus) {
        switch (executionStatus) {
            case SCENARIO_PENDING:
            case SOME_STEPS_PENDING:
                return "scenario-pending";
            case SUCCESS:
                return "scenario-successful";
            case FAILED:
                return "scenario-failed";
            default:
                return "scenario-" + executionStatus.toString().toLowerCase();
        }
    }

    static String toHumanReadableStatus(final ExecutionStatus executionStatus) {
        switch (executionStatus) {
            case SCENARIO_PENDING:
            case SOME_STEPS_PENDING:
                return ICON_BANNED;
            case SUCCESS:
                return ICON_CHECK_MARK;
            case FAILED:
                return ICON_EXCLAMATION_MARK;
            default:
                return executionStatus.toString();
        }
    }

    static String toHumanReadableStatus(final StepStatus stepStatus) {
        switch (stepStatus) {
            case PASSED:
                return ICON_CHECK_MARK;
            case FAILED:
                return ICON_EXCLAMATION_MARK;
            case SKIPPED:
                return ICON_STEP_FORWARD;
            case PENDING:
                return ICON_BANNED;
            default:
                return stepStatus.toString();
        }
    }

    static String toHumanReadableScenarioDuration(final long durationInNanos) {
        if (durationInNanos >= NANOSECONDS_PER_MILLISECOND) {
            return toHumanReadableDuration(durationInNanos);
        } else {
            return "0ms";
        }
    }

    static String toHumanReadableStepDuration(final long durationInNanos) {
        if (durationInNanos >= 10 * NANOSECONDS_PER_MILLISECOND) {
            return "(" + toHumanReadableDuration(durationInNanos) + ")";
        } else {
            return "";
        }
    }

    private static String toHumanReadableDuration(final long nanos) {
        final Duration duration = Duration.ofNanos(nanos);
        final String millisFragment = duration.getNano() / NANOSECONDS_PER_MILLISECOND + "ms";

        final long seconds = duration.getSeconds();
        final String secondsFragment = seconds > 0 ? seconds + "s " : "";

        return secondsFragment + millisFragment;
    }
}
