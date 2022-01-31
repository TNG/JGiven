package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JGivenReportExtractingExtension.class)
public class JUnit5AfterMethodTests extends ScenarioTestBase<GivenTestStage, WhenTestStage, ThenTestStage> {

    private final Scenario<GivenTestStage, WhenTestStage, ThenTestStage> scenario = createScenario();

    @Override
    public Scenario<GivenTestStage, WhenTestStage, ThenTestStage> getScenario() {
        return scenario;
    }

    @Test
    public void a_failing_JUnit_5_test() {
        given().nothing();
        when().a_step_fails();
        then().something_happened();
    }

    @Test
    public void a_succeding_JUnit5_test() {
        given().nothing();
        when().something_happens();
        then().something_happened();
    }

    @Test
    @Disabled
    public void a_skipped_JUnit5_test() {
        given().nothing();
        when().something_happens();
        then().something_happened();
    }


    @AfterEach
    public void modifyScenario() {
        getScenario().getModel().getLastScenarioModel().addCase(new ScenarioCaseModel());
    }
}
