package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Ignore;
import org.junit.Test;

public class SimpleStageRepetitionUseTest
    extends SimpleScenarioTest<SimpleStageRepetitionUseTest.RepeatLifecycleMethods> {

    @ScenarioStage
    DontRepeatLifecycleMethods dontRepeatLifecycleMethods;

    /* FIXME:
    We use the change of a stage class to detect that there is a new stage.
    Hence, repeatedly using the same stage does not work!
    */
    @Test
    @Ignore("Feature not implemented yet")
    public void lifecycle_methods_of_a_shared_stage_are_called_individually() {
        given().a_simple_stage_containing_before_and_after_methods();
        when().the_test_is_executed();
        then().the_before_stage_was_called_$_times(3).and()
            .the_after_stage_method_was_called_$_times(2); //we assert before the last call to 'afterStage'
    }

    @Test
    public void non_repeatable_methods_are_called_exactly_once() {
        dontRepeatLifecycleMethods.given().a_simple_stage_containing_before_and_after_methods();
        dontRepeatLifecycleMethods.when().the_test_is_executed();
        dontRepeatLifecycleMethods.then().the_before_stage_was_called_$_times(1).and()
            .the_after_stage_method_was_called_$_times(0);
    }

    static class DontRepeatLifecycleMethods extends AllTestSteps<DontRepeatLifecycleMethods> {
        @BeforeStage
        protected void beforeStage() {
            callBeforeStage();
        }

        @AfterStage
        protected void afterStage() {
            callAfterStage();
        }
    }

    static class RepeatLifecycleMethods extends AllTestSteps<RepeatLifecycleMethods> {
        @BeforeStage(repeatable = true)
        protected void beforeStage() {
            callBeforeStage();
        }

        @AfterStage(repeatable = true)
        protected void afterStage() {
            callAfterStage();
        }
    }

    static class AllTestSteps<T extends AllTestSteps<?>> extends Stage<T> {

        private int beforeStageCalled = 0;
        private int afterStageCalled = 0;

        protected void callBeforeStage() {
            beforeStageCalled++;
        }

        protected void callAfterStage() {
            afterStageCalled++;
        }

        T a_simple_stage_containing_before_and_after_methods() {
            return self();
        }

        T the_test_is_executed() {
            return self();
        }

        T the_after_stage_method_was_called_$_times(int numberOfCalls) {
            assertThat(afterStageCalled).isEqualTo(numberOfCalls);
            return self();
        }

        T the_before_stage_was_called_$_times(int numberOfCalls) {
            assertThat(beforeStageCalled).isEqualTo(numberOfCalls);
            return self();
        }
    }
}
