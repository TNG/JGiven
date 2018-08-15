package com.tngtech.jgiven.junit;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class BeforeStageInjectionTest extends SimpleScenarioTest<BeforeStageInjectionTest.BeforeStageInjectionStage> {

    @Rule
    @ProvidedScenarioState
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void scenario_state_should_be_available_in_BeforeStage_method() {
        given().someStep();
    }

    public static class BeforeStageInjectionStage {
        @ExpectedScenarioState
        TemporaryFolder folder;

        @BeforeStage
        public void init() {
            Assertions.assertThat( folder ).isNotNull();
        }

        void someStep() {
            Assertions.assertThat( folder ).isNotNull();
        }
    }
}
