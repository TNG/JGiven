package com.tngtech.jgiven.tests.sealed;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class SealedClassesHandlingTest {

    @Test
    void jgiven_fails_to_start_scenario_with_a_sealed_stage() {
        var scenarioTest = new SimpleScenarioTest<SealedStage>() {
            void test_sealed_stage_usage() {
                given().after();
            }
        };
        assertThatThrownBy(() -> scenarioTest.getScenario().startScenario(
            scenarioTest.getClass(),
            scenarioTest.getClass().getDeclaredMethod("test_sealed_stage_usage"),
            Collections.emptyList()
        )).hasRootCauseInstanceOf(IncompatibleClassChangeError.class);
    }

    @Test
    void jgiven_handles_non_sealed_classes() throws NoSuchMethodException {
        var scenarioTest = new SimpleScenarioTest<NonSealedSubStage>() {
            void test_non_sealed_stage_usage() {
                given().after();
            }
        };
        scenarioTest.getScenario().startScenario(
            scenarioTest.getClass(),
            scenarioTest.getClass().getDeclaredMethod("test_non_sealed_stage_usage"),
            Collections.emptyList()
        );
        assertThatCode(scenarioTest::test_non_sealed_stage_usage).doesNotThrowAnyException();
    }

    public static sealed class SealedStage<SELF extends SealedStage<SELF>> extends Stage<SELF>
        permits NonSealedSubStage {
        public SELF after() {
            return self();
        }
    }

    public static non-sealed class NonSealedSubStage extends SealedStage<NonSealedSubStage> {
    }
}
