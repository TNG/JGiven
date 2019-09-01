package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Pending;

@Pending
public class PendingOnClassTest extends SimpleScenarioTest<PendingOnClassTest.PendingTestSteps> {

    @Test
    public void pending_annotation_works_on_test_class() throws Throwable {
        when().some_action();
    }

    public static class PendingTestSteps {
        @ExpectedScenarioState
        String someState;

        public PendingTestSteps some_action() {
            assertThat(someState).isNotNull();
            return this;
        }
    }

}
