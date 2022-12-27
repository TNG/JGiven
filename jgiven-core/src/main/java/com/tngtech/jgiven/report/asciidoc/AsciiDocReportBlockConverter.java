package com.tngtech.jgiven.report.asciidoc;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.ScenarioDataTable;
import com.tngtech.jgiven.report.model.DataTable;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ReportStatistics;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.report.model.Word;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.List;

class AsciiDocReportBlockConverter implements ReportBlockConverter {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private final PrintWriter writer;

    AsciiDocReportBlockConverter(PrintWriter printWriter) {
        this.writer = printWriter;
    }


    @Override
    public String convertFeatureHeaderBlock(final String featureName, final ReportStatistics statistics,
                                            final String description) {
        StringBuilder blockContent = new StringBuilder();

        blockContent.append("=== ").append(featureName).append(NEW_LINE);

        blockContent.append(NEW_LINE);

        blockContent.append(statistics.numSuccessfulScenarios).append(" Successful, ");
        blockContent.append(statistics.numFailedScenarios).append(" Failed, ");
        blockContent.append(statistics.numPendingScenarios).append(" Pending, ");
        blockContent.append(statistics.numScenarios).append(" Total ");
        blockContent.append(toHumanReadableDuration(statistics.durationInNanos));

        if (description != null) {
            blockContent.append(NEW_LINE);
            blockContent.append(NEW_LINE);

            blockContent.append(description);
        }

        return blockContent.toString();
    }

    @Override
    public String convertScenarioHeaderBlock(final String name, final ExecutionStatus executionStatus,
                                             final long duration, final List<String> tagNames,
                                             final String extendedDescription) {
        StringBuilder blockContent = new StringBuilder();

        blockContent.append("==== ").append(WordUtil.capitalize(name)).append(NEW_LINE);

        blockContent.append(NEW_LINE);

        blockContent.append("[").append(toHumanReadableStatus(executionStatus)).append("] ")
            .append(toHumanReadableDuration(duration));


        if (extendedDescription != null && !extendedDescription.isEmpty()) {
            blockContent.append(NEW_LINE);
            blockContent.append(NEW_LINE);
            blockContent.append(extendedDescription);
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
    public String convertCaseHeaderBlock(int caseNr, List<String> parameterNames, List<String> parameterValues) {
        StringBuilder blockContent = new StringBuilder();

        blockContent.append("===== Case ").append(caseNr);

        if (parameterNames.size() >= 1) {
            blockContent.append(" ").append(parameterNames.get(0)).append(" = ").append(parameterValues.get(0));
        }

        for (int i = 1; i < parameterNames.size(); i++) {
            blockContent.append(", ").append(parameterNames.get(i)).append(" = ").append(parameterValues.get(i));
        }

        return blockContent.toString();
    }

    @Override
    public String convertStepBlock(final int depth, final List<Word> words, final StepStatus status,
                                   final long durationInNanos, final String extendedDescription,
                                   final boolean caseIsUnsuccessful, final String currentSectionTitle,
                                   boolean scenarioHasDataTable) {

        StringBuilder blockContent = new StringBuilder();

        if (currentSectionTitle != null && !currentSectionTitle.isEmpty()) {
            blockContent.append(".").append(currentSectionTitle).append(NEW_LINE);
        }

        for (int i = 0; i <= depth; i++) {
            blockContent.append("*");
        }
        blockContent.append(" ");

        appendWordFragments(blockContent, words);

        blockContent.append(buildStepEndFragment(caseIsUnsuccessful, status, durationInNanos, extendedDescription));
        return blockContent.toString();
    }

    private void appendWordFragments(final StringBuilder blockContent, final List<Word> words) {
        for (Word word : words) {
            if (word.isIntroWord()) {
                blockContent.append(buildIntroWordFragment(word.getFormattedValue()));
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

    private String buildIntroWordFragment(String word) {
        return "[.jg-introWord]*" + WordUtil.capitalize(word) + "*";
    }


    private String buildDataTableFragment(DataTable dataTable) {
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
                fragmentContent.append("|").append(cell);
            }
            fragmentContent.append(NEW_LINE);
        }
        fragmentContent.append("|===");
        return fragmentContent.toString();
    }

    private String buildDataTableHead(DataTable dataTable) {
        final String colSpec = dataTable.hasVerticalHeader()
            ? "h," + generate(() -> "1").limit(dataTable.getColumnCount() - 1).collect(joining(","))
            : generate(() -> "1").limit(dataTable.getColumnCount()).collect(joining(","));

        return "[.jg-argumentTable"
            + (dataTable.hasHorizontalHeader() ? "%header" : "")
            + ",cols=\"" + colSpec + "\"]";
    }

    private String buildParameterWordFragment(String placeHolderValue) {
        return " [.jg-argument]*<" + placeHolderValue + ">*";
    }

    private String buildArgumentWordFragment(String argumentValue) {
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

    private String buildOtherWordFragment(String word, boolean differs) {
        if (differs) {
            return " #" + word + "#";
        } else {
            return " " + word;
        }
    }

    private String buildStepEndFragment(final boolean caseIsUnsuccessful, final StepStatus status, final long duration,
                                        final String extendedDescription) {
        final String stepStatus = caseIsUnsuccessful
            ? "[.right]#[" + status + "] " + toHumanReadableDuration(duration) + "#"
            : "";


        if (extendedDescription != null && !extendedDescription.isEmpty()) {
            return stepStatus + " +\n"
                + "_" + extendedDescription + "_";
        } else {
            return stepStatus;
        }
    }

    @Override
    public void dataTable(ScenarioDataTable scenarioDataTable) {
        writer.println();
        writer.println(".Cases");
        writer.println("[options=\"header\"]");
        writer.println("|===");

        writer.print("| # ");
        for (String placeHolder : scenarioDataTable.placeHolders()) {
            writer.print(" | " + placeHolder);
        }
        writer.println(" | Status");

        for (ScenarioDataTable.Row row : scenarioDataTable.rows()) {
            writer.print("| " + row.nr());

            for (String value : row.arguments()) {
                writer.print(" | " + escapeTableValue(value));
            }

            writer.println(" | " + row.status());
        }
        writer.println("|===");
    }

    private String escapeTableValue(String value) {
        return escapeArgumentValue(value.replace("|", "\\|"));
    }

    private String escapeArgumentValue(String value) {
        // TODO Is this really necessary?
        //return "+" + value + "+";
        return value;
    }

    private static String toHumanReadableStatus(ExecutionStatus executionStatus) {
        switch (executionStatus) {
            case SCENARIO_PENDING:
                // fall through
            case SOME_STEPS_PENDING:
                return "PENDING";
            case SUCCESS:
                // fall through
            case FAILED:
                // fall through
            default:
                return executionStatus.toString();
        }
    }

    private static String toHumanReadableDuration(long nanos) {
        Duration duration = Duration.ofNanos(nanos);
        return "(" + duration.getSeconds() + "s " + duration.getNano() / 1000000 + "ms)";
    }

}
