package com.tngtech.jgiven.testframework;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class ThenLifecycleOrdering extends Stage<ThenLifecycleOrdering> {

    @ExpectedScenarioState
    protected ReportModel reportModel;

    private static final String RELEVANT_SCENARIO_NAME = "Model abused for Reporting";

    public ThenLifecycleOrdering the_framework_before_method_was_executed_before_jgivens(){
        assertThat(getRelevantCases())
                .extracting(ScenarioCaseModel::getDescription)
                .contains("framework before method called first")
                .doesNotContain("framework before method called second");
        return this;
    }

    public ThenLifecycleOrdering the_framework_after_method_was_executed_before_jgivens(){
        assertThat(getRelevantCases())
                .extracting(ScenarioCaseModel::getDescription)
                .contains("framework after method called first")
                .doesNotContain("framework after method called second");
        return this;
    }

    public ThenLifecycleOrdering the_framework_after_method_was_executed_after_jgivens(){
        assertThat(getRelevantCases())
                .extracting(ScenarioCaseModel::getDescription)
                .contains("framework after method called second")
                .doesNotContain("framework after method called first");
        return this;
    }

    private Stream<ScenarioCaseModel> getRelevantCases(){
        return reportModel.getScenarios().stream()
                .filter(scenarioModel -> RELEVANT_SCENARIO_NAME.equals(scenarioModel.getDescription()))
                .flatMap(scenarioModel -> scenarioModel.getScenarioCases().stream());
    }

}
