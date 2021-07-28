package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.exception.JGivenMissingGuaranteedScenarioStateException;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;


public class GuaranteedStateTest extends ScenarioTest<GuaranteedStateTest.GuaranteedGivenTestStage,
                                                    GuaranteedStateTest.GuaranteedWhenThenTestStage,
                                                    GuaranteedStateTest.GuaranteedWhenThenTestStage> {

    @Test(expected = JGivenMissingGuaranteedScenarioStateException.class)
    public void assure_before_method_of_second_test_is_executed_after_guaranteed_fields_validation() {
        given().a_given_stage_with_guaranteed_field_uninitialized();
        then().the_before_is_not_called();
    }

    @Test(expected = ClassNotFoundException.class)
    public void assure_before_method_of_second_test_is_executed_if_guaranteed_initialized() {
        given().a_given_stage_with_guaranteed_field_initialized();
        then().the_before_is_not_called();
    }

    static class GuaranteedGivenTestStage extends Stage<GuaranteedGivenTestStage> {
        @ProvidedScenarioState(guaranteed = true)
        Object guaranteedObject = null;

        GuaranteedGivenTestStage a_given_stage_with_guaranteed_field_uninitialized() {
            return self();
        }

        GuaranteedGivenTestStage a_given_stage_with_guaranteed_field_initialized() {
            this.guaranteedObject = "I'm initialized";
            return self();
        }
    }

    static class GuaranteedWhenThenTestStage extends Stage<GuaranteedWhenThenTestStage> {
        @BeforeStage
        public void beforeMethod() throws ClassNotFoundException {
            throw new ClassNotFoundException("Not a JGivenMissingGuaranteedScenarioStateException");
        }

        GuaranteedWhenThenTestStage the_before_is_not_called() {
            return self();
        }
    }
}
