package com.tngtech.jgiven.report.asciidoc;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.CasesTable;
import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.DataTable;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ReportStatistics;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.report.model.Word;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class AsciiDocReportBlockConverter implements ReportBlockConverter {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String ICON_CHECK_MARK = "icon:check-square[role=green]";
    private static final String ICON_EXCLAMATION_MARK = "icon:exclamation-circle[role=red]";
    private static final String ICON_BANNED = "icon:ban[role=silver]";

    @Override
    public String convertFeatureHeaderBlock(final String featureName, final ReportStatistics statistics,
                                            final String description) {
        StringBuilder blockContent = new StringBuilder();

        blockContent.append("=== ").append(featureName).append(NEW_LINE);

        blockContent.append(NEW_LINE);

        blockContent.append(ICON_CHECK_MARK).append(" ").append(statistics.numSuccessfulScenarios).append(" Successful, ");
        blockContent.append(ICON_EXCLAMATION_MARK).append(" ").append(statistics.numFailedScenarios).append(" Failed, ");
        blockContent.append(ICON_BANNED).append(" ").append(statistics.numPendingScenarios).append(" Pending, ");
        blockContent.append(statistics.numScenarios).append(" Total");
        blockContent.append(" (").append(toHumanReadableDuration(statistics.durationInNanos).orElse("0ms")).append(")");

        if (description != null && !description.isEmpty()) {
            blockContent.append(NEW_LINE);
            blockContent.append(NEW_LINE);
            blockContent.append("+++").append(description).append("+++");
        }

        return blockContent.toString();
    }

    @Override
    public String convertScenarioHeaderBlock(final String name, final ExecutionStatus executionStatus,
                                             final long duration, final List<String> tagNames,
                                             final String extendedDescription) {
        StringBuilder blockContent = new StringBuilder();

        blockContent.append("// tag::").append(toAsciiDocTag(executionStatus)).append("[]").append(NEW_LINE);
        blockContent.append(NEW_LINE);

        blockContent.append("==== ").append(WordUtil.capitalize(name)).append(NEW_LINE);

        blockContent.append(NEW_LINE);

        blockContent.append(toHumanReadableStatus(executionStatus));
        toHumanReadableDuration(duration).ifPresent(dur -> blockContent.append(" (").append(dur).append(")"));


        if (extendedDescription != null && !extendedDescription.isEmpty()) {
            blockContent.append(NEW_LINE);
            blockContent.append(NEW_LINE);
            blockContent.append("+++").append(extendedDescription).append("+++");
        }

        if (!tagNames.isEmpty()) {
            blockContent.append(NEW_LINE);
            blockContent.append(NEW_LINE);
            blockContent.append("Tags: ");
            blockContent.append(tagNames.stream().map(tag -> "_" + tag + "_").collect(joining(", ")));
        }

        return blockContent.toString();
    }

    @Override
    public String convertCaseHeaderBlock(final int caseNr, final List<String> parameterNames,
                                         final List<String> parameterValues, final String description) {
        StringBuilder blockContent = new StringBuilder();

        blockContent.append("===== Case ").append(caseNr);

        if (description != null && !description.isEmpty()) {
            blockContent.append(" ").append(description);
        }

        if (!parameterNames.isEmpty()) {
            blockContent.append(NEW_LINE).append(NEW_LINE);

            blockContent.append("====").append(NEW_LINE);
            blockContent.append(parameterNames.get(0)).append(" = ").append(parameterValues.get(0));

            for (int i = 1; i < parameterNames.size(); i++) {
                blockContent.append(", ").append(parameterNames.get(i)).append(" = ").append(parameterValues.get(i));
            }
            blockContent.append(NEW_LINE).append("====");
        }

        return blockContent.toString();
    }

    @Override
    public String convertFirstStepBlock(final int depth, final List<Word> words, final StepStatus status, final long durationInNanos,
            final String extendedDescription, final boolean caseIsUnsuccessful, boolean scenarioHasDataTable,
            final String currentSectionTitle) {

        StringBuilder blockContent = new StringBuilder();

        if (currentSectionTitle != null && !currentSectionTitle.isEmpty()) {
            blockContent.append(".").append(currentSectionTitle).append(NEW_LINE);
        }

        blockContent.append("[unstyled.jg-step-list]").append(NEW_LINE);
        blockContent.append(convertStepBlock(depth, words, status, durationInNanos, extendedDescription, caseIsUnsuccessful, scenarioHasDataTable));

        return blockContent.toString();
    }

    @Override
    public String convertStepBlock(final int depth, final List<Word> words, final StepStatus status, final long durationInNanos,
            final String extendedDescription, final boolean caseIsUnsuccessful, boolean scenarioHasDataTable) {

        StringBuilder blockContent = new StringBuilder();

        blockContent.append(buildIndentationFragment(depth, "*"));

        appendWordFragments(blockContent, words);

        blockContent.append(buildStepEndFragment(
            depth, caseIsUnsuccessful, status, durationInNanos, extendedDescription));
        return blockContent.toString();
    }

    @Override
    public String convertCasesTableBlock(final CasesTable casesTable) {
        StringBuilder blockContent = new StringBuilder();

        blockContent.append(".Cases").append(NEW_LINE);
        blockContent.append("[.jg-casesTable%header,cols=\"")
            .append(generateTableColSpec(true, casesTable.placeHolders().size() + 1))
            .append(",>1\"]").append(NEW_LINE);
        blockContent.append("|===").append(NEW_LINE);

        blockContent.append("| #");
        for (String placeHolder : casesTable.placeHolders()) {
            blockContent.append(" | ").append(placeHolder);
        }
        blockContent.append(" | Status").append(NEW_LINE);

        for (CasesTable.Row row : casesTable.rows()) {
            blockContent.append("| ").append(row.nr());

            for (String value : row.arguments()) {
                blockContent.append(" | ").append(escapeTableValue(value));
            }

            blockContent.append(" | ").append(row.status()).append(NEW_LINE);
        }
        blockContent.append("|===");
        return blockContent.toString();
    }

    @Override
    public String convertCaseFooterBlock(final String errorMessage, final List<String> stackTraceLines) {
        StringBuilder blockContent = new StringBuilder();

        final String[] errorMessageLines = errorMessage.split(NEW_LINE, 2);

        blockContent.append(".").append(errorMessageLines[0]).append(NEW_LINE);
        blockContent.append("[.jg-exception%collapsible]").append(NEW_LINE);
        blockContent.append("====").append(NEW_LINE);

        if (errorMessageLines.length > 1 && !errorMessageLines[1].isEmpty()) {
                blockContent.append(errorMessageLines[1]).append(NEW_LINE);
                blockContent.append(NEW_LINE);

        }

        if (stackTraceLines != null && !stackTraceLines.isEmpty()) {
            blockContent.append("....").append(NEW_LINE);
            stackTraceLines.forEach(line -> blockContent.append(line).append(NEW_LINE));
            blockContent.append("....").append(NEW_LINE);
        } else {
            blockContent.append("No stacktrace provided").append(NEW_LINE);
        }

        blockContent.append("====").append(NEW_LINE);

        return blockContent.toString();
    }

    @Override
    public String convertScenarioFooterBlock(ExecutionStatus executionStatus) {
        return "// end::" + toAsciiDocTag(executionStatus) + "[]";
    }

    private void appendWordFragments(final StringBuilder blockContent, final List<Word> words) {
        for (Word word : words) {
            if (word.isIntroWord()) {
                blockContent.append(" ").append(buildIntroWordFragment(word.getFormattedValue()));
            } else if (word.isDataTable()) {
                blockContent.append(buildDataTableFragment(word.getArgumentInfo().getDataTable()));
            } else if (word.isArg() && word.getArgumentInfo().isParameter()) {
                blockContent.append(buildParameterWordFragment(word.getArgumentInfo().getParameterName()));
            } else if (word.isArg()) {
                blockContent.append(buildArgumentWordFragment(word.getFormattedValue()));
            } else {
                blockContent.append(buildOtherWordFragment(word.getFormattedValue(), word.isDifferent()));
            }
        }
    }

    private String buildIntroWordFragment(final String word) {
        return "[.jg-intro-word]*" + WordUtil.capitalize(word) + "*";
    }


    private String buildDataTableFragment(final DataTable dataTable) {
        final List<List<String>> rows = dataTable.getData();
        if (rows.isEmpty()) {
            return "";
        }

        final StringBuilder fragmentContent = new StringBuilder();

        fragmentContent.append(NEW_LINE).append("+").append(NEW_LINE);
        fragmentContent.append(buildDataTableHead(dataTable)).append(NEW_LINE);

        fragmentContent.append("|===").append(NEW_LINE);
        for (List<String> row : rows) {
            for (String cell : row) {
                fragmentContent.append("| ").append(cell).append(" ");
            }
            fragmentContent.append(NEW_LINE);
        }
        fragmentContent.append("|===");
        return fragmentContent.toString();
    }

    private String buildDataTableHead(final DataTable dataTable) {
        final String colSpec = generateTableColSpec(dataTable.hasVerticalHeader(), dataTable.getColumnCount());

        return "[.jg-argumentTable"
               + (dataTable.hasHorizontalHeader() ? "%header" : "")
               + ",cols=\"" + colSpec + "\"]";
    }

    private String buildParameterWordFragment(final String placeHolderValue) {
        return " [.jg-argument]*<" + placeHolderValue + ">*";
    }

    private String buildArgumentWordFragment(final String argumentValue) {
        if (argumentValue.contains("\n")) {
            return "\n"
                   + "+\n"
                   + "[.jg-argument]\n"
                   + "....\n"
                   + argumentValue + "\n"
                   + "....\n";
        } else {
            return " [.jg-argument]_" + escapeArgumentValue(argumentValue) + "_";
        }
    }

    private String buildOtherWordFragment(final String word, final boolean differs) {
        if (differs) {
            return " #" + word + "#";
        } else {
            return " " + word;
        }
    }

    private String buildStepEndFragment(final int depth, final boolean caseIsUnsuccessful, final StepStatus status,
                                        final long duration,
                                        final String extendedDescription) {
        final String humanDuration = toHumanReadableDuration(duration).map(dur -> " (" + dur + ")").orElse("");
        final String stepStatus = caseIsUnsuccessful
            ? " " + toHumanReadableStatus(status) + humanDuration
            : "";


        if (extendedDescription != null && !extendedDescription.isEmpty()) {
            return stepStatus + " +\n"
                   + buildIndentationFragment(depth, " ") + " _+++" + extendedDescription + "+++_";
        } else {
            return stepStatus;
        }
    }

    public String convertStatisticsBlock(final Map<String, ReportStatistics> featureStatistics,
            final ReportStatistics totalStatistics) {
        final StringBuilder statisticsTable = new StringBuilder();

        statisticsTable.append(".Total Statistics").append(NEW_LINE);
        statisticsTable.append("[options=\"header,footer\"]").append(NEW_LINE);
        statisticsTable.append("|===").append(NEW_LINE);

        statisticsTable.append("| feature ");
        statisticsTable.append("| total classes ");
        statisticsTable.append("| successful scenarios ");
        statisticsTable.append("| failed scenarios ");
        statisticsTable.append("| pending scenarios ");
        statisticsTable.append("| total scenarios ");
        statisticsTable.append("| failed cases ");
        statisticsTable.append("| total cases ");
        statisticsTable.append("| total steps ");
        statisticsTable.append("| duration").append(NEW_LINE);

        featureStatistics.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(entry -> convertStatisticsRow(statisticsTable, entry.getKey(), entry.getValue()));

        convertStatisticsRow(statisticsTable, "sum", totalStatistics);

        statisticsTable.append("|===");
        return statisticsTable.toString();
    }

    private static void convertStatisticsRow(StringBuilder builder, String name, ReportStatistics statistics) {
        builder.append("| ").append(name);
        builder.append(" | ").append(statistics.numClasses);
        builder.append(" | ").append(statistics.numSuccessfulScenarios);
        builder.append(" | ").append(statistics.numFailedScenarios);
        builder.append(" | ").append(statistics.numPendingScenarios);
        builder.append(" | ").append(statistics.numScenarios);
        builder.append(" | ").append(statistics.numFailedCases);
        builder.append(" | ").append(statistics.numCases);
        builder.append(" | ").append(statistics.numSteps);
        builder.append(" | ").append(toHumanReadableDuration(statistics.durationInNanos).orElse("0ms")).append(NEW_LINE);
    }

    private String escapeTableValue(final String value) {
        return escapeArgumentValue(value.replace("|", "\\|"));
    }

    private String escapeArgumentValue(final String value) {
        // TODO Is this really necessary?
        // return "+" + value + "+";
        return value;
    }

    private static String buildIndentationFragment(final int depth, final String symbol) {
        return generate(() -> symbol).limit(depth + 1L).collect(joining());
    }

    private static String generateTableColSpec(final boolean withVerticalHeader, final int columnCount) {
        return withVerticalHeader
            ? "h," + generate(() -> "1").limit(columnCount - 1L).collect(joining(","))
            : generate(() -> "1").limit(columnCount).collect(joining(","));
    }

    private String toAsciiDocTag(final ExecutionStatus executionStatus) {
        switch (executionStatus) {
            case SCENARIO_PENDING:
                // fall through
            case SOME_STEPS_PENDING:
                return "scenario-pending";
            case SUCCESS:
                return "scenario-successful";
            case FAILED:
                return "scenario-failed";
            default:
                return "scenario-unknown";
        }
    }

    private static String toHumanReadableStatus(final ExecutionStatus executionStatus) {
        switch (executionStatus) {
            case SCENARIO_PENDING:
                // fall through
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

    private String toHumanReadableStatus(final StepStatus status) {
        switch (status) {
            case PASSED:
                return ICON_CHECK_MARK;
            case FAILED:
                return ICON_EXCLAMATION_MARK;
            case SKIPPED:
                // fall through
            case PENDING:
                return ICON_BANNED;
            default:
                return status.toString();
        }
    }

    private static Optional<String> toHumanReadableDuration(final long nanos) {
        if (nanos > 1000000) {
            final Duration duration = Duration.ofNanos(nanos);
            final long seconds = duration.getSeconds();
            if (seconds > 0) {
                return Optional.of(seconds + "s " + duration.getNano() / 1000000 + "ms");
            } else {
                return Optional.of(duration.getNano() / 1000000 + "ms");
            }
        } else {
            return Optional.empty();
        }
    }
}
