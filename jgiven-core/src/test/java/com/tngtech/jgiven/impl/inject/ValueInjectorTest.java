package com.tngtech.jgiven.impl.inject;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.exception.JGivenMissingGuaranteedScenarioStateException;
import org.junit.Test;

public class ValueInjectorTest {
    private ValueInjector injector = new ValueInjector();

    @Test(expected = JGivenMissingGuaranteedScenarioStateException.class)
    public void null_provided_field_throws_exception() {
        FakeStage stageObject = new FakeStage(null, "");

        injector.readValues(stageObject);
    }

    @Test(expected = JGivenMissingGuaranteedScenarioStateException.class)
    public void null_state_field_throws_exception() throws Throwable {
        FakeStage stageObject = new FakeStage("", null);

        injector.readValues(stageObject);
    }

    @Test
    public void initialized_fields_do_not_interrupt_execution() throws Throwable {
        FakeStage stageObject = new FakeStage("", "");

        injector.readValues(stageObject);
    }

    private class FakeStage {
        @ProvidedScenarioState(guaranteed = true)
        String providedObject;
        @ScenarioState(guaranteed = true)
        String stateObject;

        public FakeStage(String providedObject, String stateObject) {
            this.providedObject = providedObject;
            this.stateObject = stateObject;
        }
    }
}
