package com.tngtech.jgiven.junit.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Assume;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class WhenTestStep extends Stage<WhenTestStep> {
    @ExpectedScenarioState
    int value1;

    @ExpectedScenarioState
    int value2;

    @ProvidedScenarioState
    int intResult;

    public void both_values_are_multiplied_with_each_other() {
        intResult = value1 * value2;
    }

    public void multiply_with_two() {
        intResult = value1 * 2;
    }

    public void something() {}

    public void some_assertion_fails() {
        assertThat( true ).isFalse();
    }

    public void some_assumption_fails() {
        Assume.assumeTrue( false );
    }
}
