package com.tngtech.jgiven.examples.rules;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.examples.rules.RulesExampleTest.TestSteps;
import com.tngtech.jgiven.junit.ScenarioTest;

public class RulesExampleTest extends ScenarioTest<TestSteps, TestSteps, TestSteps> {

    @Test
    public void rules_work_as_expected() {
        given().resource_is_allocated();
    }

    public static class TestSteps extends Stage<TestSteps> {
        @ScenarioRule
        Rule rule = new Rule();

        void resource_is_allocated() {
            assertThat( rule.someResourceAllocated ).isTrue();
        }
    }

    static class Rule {
        private boolean someResourceAllocated;

        void before() {
            someResourceAllocated = true;
        }

        void after() {
            someResourceAllocated = false;
        }
    }

}
