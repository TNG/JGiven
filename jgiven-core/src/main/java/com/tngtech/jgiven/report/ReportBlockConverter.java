package com.tngtech.jgiven.report;

import com.google.common.collect.ListMultimap;
import com.tngtech.jgiven.report.model.CasesTable;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ReportStatistics;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.report.model.Tag;
import com.tngtech.jgiven.report.model.Word;
import java.util.List;

/**
 * Converts elements of the report model into standalone text blocks.
 */
public interface ReportBlockConverter {

    /**
     * Convert a set of execution statistics into a statistics table.
     *
     * @param featureStatistics a map from feature names to statistics
     * @param totalStatistics   the total statistics for all features combined
     */
    String convertStatisticsBlock(
            ListMultimap<String, ReportStatistics> featureStatistics,
            ReportStatistics totalStatistics);

    /**
     * Convert feature name and execution statistics into the feature header.
     *
     * @param featureName the feature's name
     * @param statistics  the execution statistics for the feature
     * @param description the description, may be {@code null}
     */
    String convertFeatureHeaderBlock(String featureName, ReportStatistics statistics, String description);

    /**
     * Converts a scenario's header information into a formatted string block.
     *
     * @param identifier          the unique identifier for the scenario
     * @param name                the name of the scenario
     * @param executionStatus     the execution status of the scenario, such as SUCCESS or FAILED
     * @param duration            the execution duration of the scenario in milliseconds
     * @param tags                a list of tags associated with the scenario
     * @param extendedDescription an extended description for the scenario; may be null
     * @return a formatted string block representing the scenario header
     */
    String convertScenarioHeaderBlock(
            String identifier,
            String name,
            ExecutionStatus executionStatus,
            long duration,
            List<Tag> tags,
            String extendedDescription);

    /**
     * Convert scenario case number and parameters into case header.
     * <p>
     * It is guaranteed that {@code parameterNames.size() == caseArgument.size()}
     *
     * @param caseNr          the number of the case, starting from 1
     * @param executionStatus whether the case was successful
     * @param duration        how long did the scenario case run?
     * @param description     a short description of this case, may be {@code null}
     */
    String convertCaseHeaderBlock(int caseNr, ExecutionStatus executionStatus, final long duration, String description);

    /**
     * Convert the words that make up the first step into a block.
     *
     * @param depth               the depth of the step
     * @param words               the words to be converted
     * @param status              was the step executed successfully?
     * @param durationInNanos     how long did the step take?
     * @param extendedDescription detailed description of the step, may be {@code null}
     * @param caseIsUnsuccessful  was the scenario case executed successfully?
     * @param currentSectionTitle the current section's title, may be {@code null}
     */
    String convertFirstStepBlock(
            int depth,
            List<Word> words,
            StepStatus status,
            long durationInNanos,
            String extendedDescription,
            boolean caseIsUnsuccessful,
            String currentSectionTitle);

    /**
     * Convert the words that make up a step into a block.
     *
     * @param depth               the depth of the step
     * @param words               the words to be converted
     * @param status              was the step executed successfully?
     * @param durationInNanos     how long did the step take?
     * @param extendedDescription detailed description of the step, may be {@code null}
     * @param caseIsUnsuccessful  was the scenario case executed successfully?
     */
    String convertStepBlock(
            int depth,
            List<Word> words,
            StepStatus status,
            long durationInNanos,
            String extendedDescription,
            boolean caseIsUnsuccessful);

    /**
     * Is invoked at the end of a scenario, when the scenario has multiple case and a data table.
     *
     * @param casesTable the data table of the scenario
     */
    String convertCasesTableBlock(CasesTable casesTable);

    /**
     * Is invoked if the case failed with an exception.
     *
     * @param errorMessage    the message describing the error
     * @param stackTraceLines the stacktrace lines if present
     */
    String convertCaseFooterBlock(String errorMessage, List<String> stackTraceLines);

    /**
     * Is invoked at the end of a scenario.
     *
     * @param identifier      the unique identifier for the scenario
     * @param executionStatus was the scenario successful?
     * @param tags            tags the scenario is tagged with
     */
    String convertScenarioFooterBlock(final String identifier, ExecutionStatus executionStatus, List<Tag> tags);

}
