package com.tngtech.jgiven.report;

import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ReportStatistics;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.report.model.Word;
import java.util.List;

public interface ReportBlockConverter {

    /**
     * Convert feature name and execution statistics into feature header.
     *
     * @param featureName the feature's name
     * @param statistics  the execution statistics for the feature
     * @param description the description, may be {@code null}
     */
    String convertFeatureHeaderBlock(String featureName, ReportStatistics statistics, String description);

    /**
     * Convert scenario name and more meta information into scenario header.
     * <p>
     * The name corresponds to the test method name
     * </p>
     *
     * @param name                the name of the scenario
     * @param executionStatus     was the scenario successful
     * @param duration            how long did the scenario run
     * @param tagNames            names of the tags if scenario is tagged
     * @param extendedDescription detailed description of the scenario, may be {@code null}
     */
    String convertScenarioHeaderBlock(String name, ExecutionStatus executionStatus, long duration,
                                      List<String> tagNames, String extendedDescription);

    /**
     * Convert scenario case number and parameters into case header.
     * <p>
     * It is guaranteed that {@code parameterNames.size() == caseArgument.size()}
     *
     * @param caseNr          the number of the case, starting from 1
     * @param parameterNames  the parameter names of the scenario
     * @param parameterValues the arguments of the case
     */
    String convertCaseHeaderBlock(int caseNr, List<String> parameterNames, List<String> parameterValues);

    String convertStepBlock(int depth, List<Word> words, StepStatus status, long durationInNanos,
                            String extendedDescription, boolean caseIsUnsuccessful, String currentSectionTitle,
                            boolean scenarioHasDataTable);

    /**
     * Is invoked at the end of a scenario, when the scenario has multiple case and a data table.
     *
     * @param scenarioDataTable the data table of the scenario
     */
    void dataTable(ScenarioDataTable scenarioDataTable);
}
