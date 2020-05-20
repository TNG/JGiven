package com.tngtech.jgiven.examples;

import com.tngtech.jgiven.examples.tags.FailingOnPurpose;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.tags.Issue;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@FailingOnPurpose
public class FailingScenarioTest extends SimpleScenarioTest<FailingScenarioTest.Steps> {



    @Test
    @Issue("#234")
    public void a_scenario_with_a_multi_line_error_message() {
        given().multi_line_error_message();
    }

    public static class Steps {
        public Steps multi_line_error_message() {
            assertThat(true).as("This\nmessage\nhas\nmultiple lines").isFalse();
            return this;
        }
    }
}
