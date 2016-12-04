package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.junit.ClassRule;
import org.junit.Rule;

import com.tngtech.jgiven.Stage;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that JGiven can be used without inheriting from any class,
 * just by using the two JGiven rules
 */
public class OnlyRulesTest {

    @ClassRule
    public static final JGivenClassRule writerRule = new JGivenClassRule();

    @Rule
    public final JGivenMethodRule scenarioRule = new JGivenMethodRule();

    @ScenarioStage
    TestStage stage;

    @Test
    public void JGiven_can_be_used_just_by_using_JUnit_rules() {
        stage.given().something();

        assertThat(scenarioRule.getScenario().getScenarioModel().getCase(0).getFirstStep().getLastWord().getValue()).isEqualTo("something");
    }

    public static class TestStage extends Stage<TestStage> {
        public void something() {

        }
    }

}
