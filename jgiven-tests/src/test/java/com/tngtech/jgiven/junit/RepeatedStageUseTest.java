package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.annotation.ScenarioState;
import org.junit.Test;

public class RepeatedStageUseTest extends ScenarioTest<
    RepeatedStageUseTest.GivenStage,
    RepeatedStageUseTest.StageWithAfterStageMethods,
    RepeatedStageUseTest.ThenStage> {

    @ScenarioStage
    private StageWithBeforeStageMethods whenBeforeStage;

    @Test
    public void after_stage_method_is_executed_multiple_times() {
        given().a_scenario_step();
        when().a_stage_is_completed();
        then().non_repeatable_lifecycle_method_ran(true)
            .and().repeatable_lifecycle_method_ran(true);

        given().a_scenario_step();
        when().a_stage_is_completed();
        then().repeatable_lifecycle_method_ran(true)
            .and().non_repeatable_lifecycle_method_ran(false);
    }

    @Test
    public void before_stage_method_is_executed_multiple_times() {
        given().a_scenario_step();
        whenBeforeStage.a_stage_is_completed();
        then().non_repeatable_lifecycle_method_ran(true)
            .and().repeatable_lifecycle_method_ran(true);

        given().a_scenario_step();
        whenBeforeStage.a_stage_is_completed();
        then().repeatable_lifecycle_method_ran(true)
            .and().non_repeatable_lifecycle_method_ran(false);
    }

    static class GivenStage {
        @ProvidedScenarioState
        private boolean repeateableHasRun;

        @ProvidedScenarioState
        private boolean nonRepeateableHasRun;

        public GivenStage a_scenario_step() {
            repeateableHasRun = false;
            nonRepeateableHasRun = false;
            return this;
        }
    }

    static class StageWithBeforeStageMethods {
        @ScenarioState
        private boolean repeateableHasRun;

        @ScenarioState
        private boolean nonRepeateableHasRun;

        @BeforeStage(repeatable = true)
        public void repeateable_before_stage() {
            repeateableHasRun = true;
        }

        @BeforeStage
        public void repeatable_before_stage() {
            nonRepeateableHasRun = true;
        }

        public StageWithBeforeStageMethods a_stage_is_completed() {
            return this;
        }
    }

    static class StageWithAfterStageMethods {

        @ScenarioState
        private boolean repeateableHasRun;

        @ScenarioState
        private boolean nonRepeateableHasRun;

        public StageWithAfterStageMethods a_stage_is_completed() {
            repeateableHasRun = false;
            nonRepeateableHasRun = false;
            return this;
        }

        @AfterStage(repeatable = true)
        public void set_hasRun_to_True() {
            repeateableHasRun = true;
        }

        @AfterStage
        public void set_alsoHasRun_to_True() {
            nonRepeateableHasRun = true;
        }
    }

    static class ThenStage extends Stage<ThenStage> {
        @ExpectedScenarioState
        private boolean repeateableHasRun;

        @ExpectedScenarioState
        private boolean nonRepeateableHasRun;

        public ThenStage repeatable_lifecycle_method_ran(boolean value) {
            assertThat(repeateableHasRun).isEqualTo(value);
            return this;
        }

        public ThenStage non_repeatable_lifecycle_method_ran(boolean value) {
            assertThat(nonRepeateableHasRun).isEqualTo(value);
            return this;
        }
    }
}

