package com.tngtech.jgiven.junit;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.exception.JGivenMissingRequiredScenarioStateException;
import com.tngtech.jgiven.junit.test.BeforeAfterTestStage;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;

@RunWith( DataProviderRunner.class )
@JGivenConfiguration( TestConfiguration.class )
public class RequiredScenarioStateTest extends ScenarioTest<BeforeAfterTestStage, WhenTestStep, ThenTestStep> {

    static class StageWithMissingScenarioState {
        @ScenarioState( required = true )
        Boolean state;

        public void something() {}
    }

    @Test( expected = JGivenMissingRequiredScenarioStateException.class )
    public void required_states_must_be_present() throws Throwable {
        StageWithMissingScenarioState stage = addStage( StageWithMissingScenarioState.class );
        stage.something();
    }

    static class StageWithMissingExpectedScenarioState {
        @ExpectedScenarioState( required = true )
        Boolean state;

        public void something() {}
    }

    @Test( expected = JGivenMissingRequiredScenarioStateException.class )
    public void required__expected_states_must_be_present() throws Throwable {
        StageWithMissingExpectedScenarioState stage = addStage( StageWithMissingExpectedScenarioState.class );
        stage.something();
    }

    static class ProviderStage {
        @ScenarioState
        Boolean state;

        public void provide() {
            this.state = true;
        }
    }

    @Test
    public void scenarios_pass_if_required_state_is_provided_by_another_stage() throws Throwable {
        ProviderStage stage = addStage( ProviderStage.class );
        StageWithMissingScenarioState stage2 = addStage( StageWithMissingScenarioState.class );

        stage.provide();
        stage2.something();
    }

}
