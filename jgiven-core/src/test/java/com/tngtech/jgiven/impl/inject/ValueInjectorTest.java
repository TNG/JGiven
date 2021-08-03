package com.tngtech.jgiven.impl.inject;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.exception.JGivenMissingGuaranteedScenarioStateException;
import com.tngtech.jgiven.exception.JGivenMissingRequiredScenarioStateException;
import org.junit.Test;

public class ValueInjectorTest {
    private ValueInjector injector = new ValueInjector();

    @Test(expected = JGivenMissingGuaranteedScenarioStateException.class)
    public void null_provided_field_throws_exception() {
        FakeStage stageObject = new FakeStage(null, null, "");

        injector.readValues(stageObject);
    }

    @Test(expected = JGivenMissingGuaranteedScenarioStateException.class)
    public void null_state_field_throws_exception() throws Throwable {
        FakeStage stageObject = new FakeStage("", null, null);

        injector.readValues(stageObject);
    }

    @Test
    public void initialized_fields_do_not_interrupt_execution() {
        FakeStage stageObject = new FakeStage("", null, "");

        injector.readValues(stageObject);
    }

    @Test(expected = JGivenMissingRequiredScenarioStateException.class)
    public void null_expected_field_throws_exception() {
        FakeStage stageObject = new FakeStage(null, null, "");

        injector.updateValues(stageObject);
    }

    @Test(expected = JGivenMissingRequiredScenarioStateException.class)
    public void null_expected_state_field_throws_exception() {
        FakeStage stageObject = new FakeStage("", "", null);

        injector.updateValues(stageObject);
    }

    @Test
    public void initialized_expected_fields_do_not_interrupt_execution() {
        FakeStage stageObject = new FakeStage("", "", "");

        injector.readValues(stageObject); //update field value in cache
        injector.injectValueByName("providedExpectedString", "Test");
        injector.updateValues(stageObject);

        assertThat(stageObject.providedExpectedString).isEqualTo("Test");
    }

    private class FakeStage {
        @ProvidedScenarioState(guaranteed = true)
        String providedObject;
        @ExpectedScenarioState(required = true)
        String providedExpectedString;
        @ScenarioState(guaranteed = true, required = true)
        String stateObject;

        public FakeStage(String providedObject, String providedExpectedString, String stateObject) {
            this.providedObject = providedObject;
            this.providedExpectedString = providedExpectedString;
            this.stateObject = stateObject;
        }
    }
}
