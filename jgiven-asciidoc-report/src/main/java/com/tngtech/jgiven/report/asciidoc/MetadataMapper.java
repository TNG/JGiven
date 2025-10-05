package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.StepStatus;
import java.time.Duration;

final class MetadataMapper {
    private static final String ICON_CHECK_MARK = "icon:check-square[role=green]";
    private static final String ICON_EXCLAMATION_MARK = "icon:exclamation-circle[role=red]";
    private static final String ICON_BANNED = "icon:ban[role=silver]";
    private static final String ICON_TIMES_CIRCLE = "icon:times-circle[role=gray]";
    private static final String ICON_STEP_FORWARD = "icon:step-forward[role=silver]";
    private static final int NANOSECONDS_PER_MILLISECOND = 1000000;

    private MetadataMapper() {
        // static helper class isn't intended to be instantiated
    }

    public static String toAsciiDocStartTag(final String scenarioName) {
        return "// tag::" + toAsciiDocTagName(scenarioName) + "[]";
    }

    static String toAsciiDocEndTag(final String scenarioName) {
        return "// end::" + toAsciiDocTagName(scenarioName) + "[]";
    }

    static String toAsciiDocTagName(final String scenarioName) {
        return "scenario-" + scenarioName;
    }

    static String toAsciiDocStartTag(ExecutionStatus executionStatus) {
        return "// tag::" + toAsciiDocTagName(executionStatus) + "[]";
    }

    static String toAsciiDocEndTag(ExecutionStatus executionStatus) {
        return "// end::" + toAsciiDocTagName(executionStatus) + "[]";
    }

    static String toAsciiDocTagName(final ExecutionStatus executionStatus) {
        return switch (executionStatus) {
            case SUCCESS -> "status-is-successful";
            case SCENARIO_PENDING, SOME_STEPS_PENDING -> "status-is-pending";
            case ABORTED -> "status-is-aborted";
            case FAILED -> "status-is-failed";
        };
    }

    static String toHumanReadableStatus(final ExecutionStatus executionStatus) {
        return switch (executionStatus) {
            case SUCCESS -> ICON_CHECK_MARK;
            case SCENARIO_PENDING, SOME_STEPS_PENDING -> ICON_BANNED;
            case ABORTED -> ICON_TIMES_CIRCLE;
            case FAILED -> ICON_EXCLAMATION_MARK;
        };
    }

    static String toHumanReadableStatus(final StepStatus stepStatus) {
        return switch (stepStatus) {
            case PASSED -> ICON_CHECK_MARK;
            case SKIPPED -> ICON_STEP_FORWARD;
            case PENDING -> ICON_BANNED;
            case ABORTED -> ICON_TIMES_CIRCLE;
            case FAILED -> ICON_EXCLAMATION_MARK;
        };
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
        final var duration = Duration.ofNanos(nanos);
        final var millisFragment = duration.getNano() / NANOSECONDS_PER_MILLISECOND + "ms";

        final var seconds = duration.getSeconds();
        final var secondsFragment = seconds > 0 ? seconds + "s " : "";

        return secondsFragment + millisFragment;
    }
}
