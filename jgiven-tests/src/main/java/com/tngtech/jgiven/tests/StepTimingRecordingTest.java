package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.FillerWord;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;

public class StepTimingRecordingTest extends SimpleScenarioTest<StepTimingRecordingTest.TestSteps> {

    @Test
    @org.junit.jupiter.api.Test
    public void last_step_is_preceeded_by_step() throws InterruptedException {
        given().a_step_method_whose_execution_time_wont_be_displayed()
            .a_step_method_whose_execution_time_will_be_displayed();
    }

    @Test
    @org.junit.jupiter.api.Test
    public void last_step_is_preceeded_by_intro_word() throws InterruptedException {
        given().a_step_method_whose_execution_time_wont_be_displayed()
            .and().a_step_method_whose_execution_time_will_be_displayed();
    }

    @Test
    @org.junit.jupiter.api.Test
    public void last_step_is_succeeded_by_intro_word() throws InterruptedException {
        given().a_step_method_whose_execution_time_will_be_displayed().and();
    }

    @Test
    @org.junit.jupiter.api.Test
    public void last_step_is_succeeded_by_filler_word() throws InterruptedException {
        given().a_step_method_whose_execution_time_will_be_displayed().before();
    }

    @Test
    @org.junit.jupiter.api.Test
    public void last_step_is_preceeded_by_filler_word() throws InterruptedException {
        given().before().a_step_method_whose_execution_time_will_be_displayed();
    }

    static class TestSteps extends Stage<TestSteps> {

        @FillerWord
        TestSteps before() {
            return this;
        }

        TestSteps a_step_method_whose_execution_time_wont_be_displayed() {
            return this;
        }

        TestSteps a_step_method_whose_execution_time_will_be_displayed() throws InterruptedException {
            Thread.sleep(15);
            return this;
        }
    }
}
