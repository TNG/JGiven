package com.tngtech.jgiven.junit5.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit5.JGivenExtension;

@RunWith( JUnitPlatform.class )
@ExtendWith( { JGivenExtension.class } )
@DisplayName( "@Nested")
public class NestedTest {

    @ScenarioStage
    GeneralStage outerStage;

    @ScenarioState
    String outerState = "Outer State";

    @Nested
    @DisplayName("1st level nesting")
    class NestedTestClass {

        @ScenarioStage
        NestedStage stage;

        @ScenarioState
        String nestedState = "Nested State";

        @BeforeEach
        public void background() {
            stage.some_background();
        }

        @Test
        public void nested_classes() {
            assertThat( outerStage ).as( "outerStage" ).isNotNull();
            assertThat( stage ).as( "stage" ).isNotNull();

            outerStage.some_state();
            stage.some_action();
            stage.some_outcome();
        }

        @Test
        public void another_test() {

        }

        @Nested
        @DisplayName("2nd level nesting")
        class NestedDeeper {
            @Test
            public void deeply_nested_classes() {
                assertThat( outerStage ).as( "outerStage" ).isNotNull();
                assertThat( stage ).as( "stage" ).isNotNull();

                outerStage.some_state();
                stage.some_action();
                stage.some_outcome();
            }

        }
    }

    static class NestedStage {
        @ExpectedScenarioState
        String outerState;

        @ExpectedScenarioState
        String nestedState;

        @BeforeStage
        void setup() {
            assertThat( outerState ).as( "outerState" ).isNotNull();
            assertThat( nestedState ).as( "nestedState" ).isNotNull();
        }

        public void some_action() {

        }

        public void some_outcome() {}

        public void some_background() {

        }
    }

}
