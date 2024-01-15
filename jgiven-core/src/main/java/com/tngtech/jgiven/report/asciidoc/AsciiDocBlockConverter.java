package com.tngtech.jgiven.report.asciidoc;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.model.CasesTable;
import com.tngtech.jgiven.report.model.DataTable;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ReportStatistics;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.report.model.Tag;
import com.tngtech.jgiven.report.model.Word;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class AsciiDocBlockConverter implements ReportBlockConverter {

    private static final String LINE_BREAK = System.lineSeparator();
    private static final Pattern MULTILINE_PATTERN = Pattern.compile("\\R");

    @Override
    public String convertStatisticsBlock(final ListMultimap<String, ReportStatistics> featureStatistics,
                                         final ReportStatistics totalStatistics) {
        final StringBuilder statisticsTable = new StringBuilder();

        statisticsTable.append(".Total Statistics").append(LINE_BREAK);
        statisticsTable.append("[.jg-statisticsTable%autowidth%header%footer]").append(LINE_BREAK);
        statisticsTable.append("|===").append(LINE_BREAK);

        statisticsTable.append("| feature ");
        statisticsTable.append("| total classes ");
        statisticsTable.append("| successful scenarios ");
        statisticsTable.append("| failed scenarios ");
        statisticsTable.append("| pending scenarios ");
        statisticsTable.append("| total scenarios ");
        statisticsTable.append("| failed cases ");
        statisticsTable.append("| total cases ");
        statisticsTable.append("| total steps ");
        statisticsTable.append("| duration").append(LINE_BREAK);

        featureStatistics.entries().stream().sorted(Map.Entry.comparingByKey())
                .forEach(entry -> appendStatisticsRowFragment(statisticsTable, entry.getKey(), entry.getValue()));

        appendStatisticsRowFragment(statisticsTable, "sum", totalStatistics);

        statisticsTable.append("|===");
        return statisticsTable.toString();
    }

    @Override
    public String convertFeatureHeaderBlock(final String featureName, final ReportStatistics statistics,
                                            final String description) {
        StringBuilder blockContent = new StringBuilder();

        blockContent.append("=== ").append(featureName).append(LINE_BREAK);
        blockContent.append(LINE_BREAK);

        blockContent.append(MetadataMapper.toHumanReadableStatus(ExecutionStatus.SUCCESS)).append(" ")
                .append(statistics.numSuccessfulScenarios).append(" Successful, ");
        blockContent.append(MetadataMapper.toHumanReadableStatus(ExecutionStatus.FAILED)).append(" ")
                .append(statistics.numFailedScenarios).append(" Failed, ");
        blockContent.append(MetadataMapper.toHumanReadableStatus(ExecutionStatus.SCENARIO_PENDING)).append(" ")
                .append(statistics.numPendingScenarios).append(" Pending, ");
        blockContent.append(statistics.numScenarios).append(" Total");
        blockContent.append(" (").append(MetadataMapper.toHumanReadableScenarioDuration(statistics.durationInNanos))
                .append(")");

        if (description != null && !description.isEmpty()) {
            blockContent.append(LINE_BREAK);
            blockContent.append(LINE_BREAK);
            blockContent.append("+++").append(description).append("+++");
        }

        return blockContent.toString();
    }

    @Override
    public String convertScenarioHeaderBlock(final String name, final ExecutionStatus executionStatus,
                                             final long duration, final List<Tag> tags,
                                             final String extendedDescription) {
        StringBuilder blockContent = new StringBuilder();

        blockContent.append(MetadataMapper.toAsciiDocStartTag(executionStatus)).append(LINE_BREAK);

        tags.forEach(tag -> blockContent.append(TagMapper.toAsciiDocStartTag(tag)).append(LINE_BREAK));

        blockContent.append(LINE_BREAK);

        blockContent.append("==== ").append(WordUtil.capitalize(name)).append(LINE_BREAK);

        blockContent.append(LINE_BREAK);

        blockContent.append(MetadataMapper.toHumanReadableStatus(executionStatus));
        blockContent.append(" (").append(MetadataMapper.toHumanReadableScenarioDuration(duration)).append(")");

        if (extendedDescription != null && !extendedDescription.isEmpty()) {
            blockContent.append(LINE_BREAK);
            blockContent.append(LINE_BREAK);
            blockContent.append("+++").append(extendedDescription).append("+++");
        }

        if (!tags.isEmpty()) {
            blockContent.append(LINE_BREAK);
            blockContent.append(LINE_BREAK);
            blockContent.append("Tags: ");
            blockContent.append(tags.stream().map(tag -> "_" + TagMapper.toHumanReadableLabel(tag) + "_").collect(joining(", ")));
        }

        return blockContent.toString();
    }

    @Override
    public String convertCaseHeaderBlock(final int caseNr, final ExecutionStatus executionStatus,
                                         final long duration, final String description) {
        StringBuilder blockContent = new StringBuilder();

        blockContent.append("===== Case ").append(caseNr);

        if (description != null && !description.isEmpty()) {
            blockContent.append(": ").append(description);
        }

        blockContent.append(LINE_BREAK).append(LINE_BREAK)
                .append(MetadataMapper.toHumanReadableStatus(executionStatus))
                .append(" (").append(MetadataMapper.toHumanReadableScenarioDuration(duration)).append(")");

        return blockContent.toString();
    }

    @Override
    public String convertFirstStepBlock(final int depth, final List<Word> words, final StepStatus status,
                                        final long durationInNanos,
                                        final String extendedDescription, final boolean caseIsUnsuccessful,
                                        final String currentSectionTitle) {

        StringBuilder blockContent = new StringBuilder();

        if (currentSectionTitle != null && !currentSectionTitle.isEmpty()) {
            blockContent.append(".").append(currentSectionTitle).append(LINE_BREAK);
        }

        blockContent.append("[unstyled.jg-step-list]").append(LINE_BREAK);
        blockContent.append(convertStepBlock(
                depth, words, status, durationInNanos, extendedDescription, caseIsUnsuccessful));

        return blockContent.toString();
    }

    @Override
    public String convertStepBlock(final int depth, final List<Word> words, final StepStatus status,
                                   final long durationInNanos,
                                   final String extendedDescription, final boolean caseIsUnsuccessful) {

        StringBuilder blockContent = new StringBuilder();

        blockContent.append(buildIndentationFragment(depth));

        final String statusFragment = buildStepStatusFragment(caseIsUnsuccessful, status, durationInNanos);
        final boolean lastFragmentIsBlock = appendWordFragments(blockContent, words, statusFragment);

        final String extendedDescriptionFragment = buildExtendedDescriptionFragment(
                lastFragmentIsBlock, extendedDescription);
        blockContent.append(extendedDescriptionFragment);

        return blockContent.toString();
    }

    @Override
    public String convertCasesTableBlock(final CasesTable casesTable) {
        StringBuilder blockContent = new StringBuilder();

        final int columnCount = (casesTable.hasDescriptions() ? 2 : 1) + casesTable.placeholders().size();
        final String headerColumns = generateVerticalHeaderColumns(columnCount, "~");

        blockContent.append(".Cases").append(LINE_BREAK);
        blockContent.append("[.jg-casesTable%header,cols=\"").append(headerColumns).append(",<11\"]")
                .append(LINE_BREAK);
        blockContent.append("|===").append(LINE_BREAK);

        blockContent.append("| #");
        if (casesTable.hasDescriptions()) {
            blockContent.append(" | Description");
        }
        for (String placeHolder : casesTable.placeholders()) {
            blockContent.append(" | ").append(placeHolder);
        }
        blockContent.append(" | Status").append(LINE_BREAK);

        for (CasesTable.CaseRow caseRow : casesTable.rows()) {
            convertCaseRow(blockContent, columnCount, caseRow);
        }
        blockContent.append("|===");
        return blockContent.toString();
    }

    private static void convertCaseRow(final StringBuilder blockContent, final int columnCount,
                                       final CasesTable.CaseRow caseRow) {
        Optional<String> errorMessage = caseRow.errorMessage();

        blockContent.append(errorMessage.isPresent() ? ".2+| " : "| ").append(caseRow.rowNumber());

        caseRow.description().ifPresent(description ->
                blockContent.append(" | ").append(escapeTableValue(description)));
        for (String value : caseRow.arguments()) {
            blockContent.append(" | ").append(escapeTableValue(value));
        }

        blockContent.append(" | ").append(MetadataMapper.toHumanReadableStatus(caseRow.status()))
                .append(" (").append(MetadataMapper.toHumanReadableScenarioDuration(caseRow.durationInNanos()))
                .append(")").append(LINE_BREAK);

        if (errorMessage.isPresent()) {
            List<String> stackTraceLines = caseRow.stackTrace();

            blockContent.append(columnCount).append("+a|").append(LINE_BREAK);
            appendErrorFragment(blockContent, errorMessage.get(), stackTraceLines);
        }
    }

    @Override
    public String convertCaseFooterBlock(final String errorMessage, final List<String> stackTraceLines) {
        StringBuilder blockContent = new StringBuilder();

        appendErrorFragment(blockContent, errorMessage, stackTraceLines);

        return blockContent.toString();
    }

    @Override
    public String convertScenarioFooterBlock(final ExecutionStatus executionStatus, final List<Tag> tags) {
        StringBuilder blockContent = new StringBuilder();

        Lists.reverse(tags).forEach(tag -> blockContent.append(TagMapper.toAsciiDocEndTag(tag)).append(LINE_BREAK));

        blockContent.append(MetadataMapper.toAsciiDocEndTag(executionStatus));

        return blockContent.toString();
    }

    private static boolean appendWordFragments(final StringBuilder blockContent, final List<Word> words,
                                               final String statusFragment) {
        boolean statusAppended = false;
        boolean lastFragmentWasBlockFragment = false;
        for (Word word : words) {
            if (word.isIntroWord()) {
                statusAppended |= appendFragment(blockContent, lastFragmentWasBlockFragment, statusFragment,
                        statusAppended, buildIntroWordFragment(word.getFormattedValue()));
                lastFragmentWasBlockFragment = false;
            } else if (word.isDataTable()) {
                statusAppended |= appendFragment(blockContent, lastFragmentWasBlockFragment, statusFragment,
                        statusAppended, buildDataTableFragment(word.getArgumentInfo().getDataTable()));
                lastFragmentWasBlockFragment = true;
            } else if (word.isArg() && word.getArgumentInfo().isParameter()) {
                statusAppended |= appendFragment(blockContent, lastFragmentWasBlockFragment, statusFragment,
                        statusAppended, buildParameterWordFragment(word.getArgumentInfo().getParameterName()));
                lastFragmentWasBlockFragment = false;
            } else if (word.isArg()) {
                final String argumentValue = word.getFormattedValue();
                if (argumentContainsLineBreaks(argumentValue)) {
                    statusAppended |= appendFragment(blockContent, lastFragmentWasBlockFragment, statusFragment,
                            statusAppended, buildBlockArgumentFragment(argumentValue));
                    lastFragmentWasBlockFragment = true;
                } else {
                    statusAppended |= appendFragment(blockContent, lastFragmentWasBlockFragment, statusFragment,
                            statusAppended, buildInlineArgumentFragment(argumentValue));
                    lastFragmentWasBlockFragment = false;
                }
            } else {
                statusAppended |= appendFragment(blockContent, lastFragmentWasBlockFragment, statusFragment,
                        statusAppended, buildOtherWordFragment(word.getFormattedValue(), word.isDifferent()));
                lastFragmentWasBlockFragment = false;
            }
        }
        if (!statusAppended && !statusFragment.isBlank()) {
            blockContent.append(" ").append(statusFragment);
        }

        return lastFragmentWasBlockFragment;
    }

    private static boolean argumentContainsLineBreaks(String argumentValue) {
        return MULTILINE_PATTERN.matcher(argumentValue).find();
    }

    private static boolean appendFragment(final StringBuilder blockContent, final boolean lastFragmentWasBlockFragment,
                                          final String statusFragment, final boolean statusAlreadyAppended,
                                          final String fragment) {
        final String lineContinuation = lastFragmentWasBlockFragment ? "" : " ";
        if (fragment.contains(LINE_BREAK)) {
            if (!statusAlreadyAppended && !statusFragment.isBlank()) {
                blockContent.append(" ").append(statusFragment);
            }
            blockContent.append(fragment);
            return true;
        } else {
            blockContent.append(lineContinuation).append(fragment);
            return false;
        }
    }

    private static void appendErrorFragment(
            final StringBuilder blockContent, final String errorMessage, final List<String> stackTraceLines) {
        blockContent.append("[.jg-exception]").append(LINE_BREAK);
        blockContent.append("====").append(LINE_BREAK);
        blockContent.append("[%hardbreaks]").append(LINE_BREAK);
        blockContent.append(errorMessage).append(LINE_BREAK);
        blockContent.append(LINE_BREAK);

        if (stackTraceLines != null && !stackTraceLines.isEmpty()) {
            blockContent.append(".Show stacktrace").append(LINE_BREAK);
            blockContent.append("[%collapsible]").append(LINE_BREAK);
            blockContent.append("=====").append(LINE_BREAK);
            blockContent.append("....").append(LINE_BREAK);
            stackTraceLines.forEach(line -> blockContent.append(line).append(LINE_BREAK));
            blockContent.append("....").append(LINE_BREAK);
            blockContent.append("=====").append(LINE_BREAK);
        } else {
            blockContent.append("No stacktrace provided").append(LINE_BREAK);
        }

        blockContent.append("====").append(LINE_BREAK);
    }

    private static void appendStatisticsRowFragment(final StringBuilder builder, final String name,
                                                    final ReportStatistics statistics) {
        builder.append("| ").append(name);
        builder.append(" | ").append(statistics.numClasses);
        builder.append(" | ").append(statistics.numSuccessfulScenarios);
        builder.append(" | ").append(statistics.numFailedScenarios);
        builder.append(" | ").append(statistics.numPendingScenarios);
        builder.append(" | ").append(statistics.numScenarios);
        builder.append(" | ").append(statistics.numFailedCases);
        builder.append(" | ").append(statistics.numCases);
        builder.append(" | ").append(statistics.numSteps);
        builder.append(" | ").append(MetadataMapper.toHumanReadableScenarioDuration(statistics.durationInNanos))
                .append(LINE_BREAK);
    }

    private static String buildStepStatusFragment(final boolean caseIsUnsuccessful, final StepStatus status,
                                                  final long duration) {
        final String humanReadableStatus = caseIsUnsuccessful ? MetadataMapper.toHumanReadableStatus(status) : "";
        final String humanReadableStepDuration = MetadataMapper.toHumanReadableStepDuration(duration);

        return Stream.of(humanReadableStatus, humanReadableStepDuration)
                .filter(Predicate.not(String::isBlank))
                .collect(joining(" "));
    }

    private static String buildIntroWordFragment(final String word) {
        return "[.jg-intro-word]*" + WordUtil.capitalize(word) + "*";
    }

    private static String buildDataTableFragment(final DataTable dataTable) {
        final List<List<String>> rows = dataTable.getData();
        if (rows.isEmpty()) {
            return "";
        }

        final StringBuilder fragmentContent = new StringBuilder();

        fragmentContent.append(LINE_BREAK).append("+").append(LINE_BREAK);
        fragmentContent.append(buildDataTableHead(dataTable)).append(LINE_BREAK);

        fragmentContent.append("|===").append(LINE_BREAK);
        for (List<String> row : rows) {
            for (String cell : row) {
                fragmentContent.append("| ").append(cell).append(" ");
            }
            fragmentContent.append(LINE_BREAK);
        }
        fragmentContent.append("|===");
        return fragmentContent.toString();
    }

    private static String buildDataTableHead(final DataTable dataTable) {
        final int columnCount = dataTable.getColumnCount();
        final String colSpec = dataTable.hasVerticalHeader()
                ? generateVerticalHeaderColumns(columnCount, "1")
                : generateHorizontalHeaderColumns(columnCount);

        return "[.jg-argumentTable%autowidth"
                + (dataTable.hasHorizontalHeader() ? "%header" : "")
                + ",cols=\"" + colSpec + "\"]";
    }

    private static String buildParameterWordFragment(final String placeHolderValue) {
        return "[.jg-argument]*<" + placeHolderValue + ">*";
    }

    private static String buildInlineArgumentFragment(final String argumentValue) {
        return "[.jg-argument]_" + escapeArgumentValue(argumentValue) + "_";
    }

    private static String buildBlockArgumentFragment(final String argumentValue) {
        final String delimiter = argumentValue.contains("....") ? "....." : "....";
        return LINE_BREAK
                + "+" + LINE_BREAK
                + "[.jg-argument]" + LINE_BREAK
                + delimiter + LINE_BREAK
                + argumentValue + LINE_BREAK
                + delimiter + LINE_BREAK;
    }

    private static String buildOtherWordFragment(final String word, final boolean differs) {
        if (differs) {
            return "#" + word + "#";
        } else {
            return word;
        }
    }

    private static String buildExtendedDescriptionFragment(final boolean lastFragmentIsBlock,
                                                           final String extendedDescription) {

        String fragment = "";
        if (extendedDescription != null && !extendedDescription.isEmpty()) {
            if (!lastFragmentIsBlock) {
                fragment += " +" + LINE_BREAK;
            }
            fragment += "icon:plus-circle[title=Extended Description] _+++" + extendedDescription + "+++_";
        }
        return fragment;
    }

    private static String escapeTableValue(final String value) {
        return "+" + value.replace("|", "\\|") + "+";
    }

    private static String escapeArgumentValue(final String value) {
        return "++" + value + "++";
    }

    private static String buildIndentationFragment(final int depth) {
        return generate(() -> "*").limit(depth + 1L).collect(joining());
    }

    private static String generateVerticalHeaderColumns(final int tableColumns, final String columnSpec) {
        return "h," + generate(() -> columnSpec).limit(tableColumns - 1L).collect(joining(","));
    }

    private static String generateHorizontalHeaderColumns(final int columnCount) {
        return generate(() -> "1").limit(columnCount).collect(joining(","));
    }

}
