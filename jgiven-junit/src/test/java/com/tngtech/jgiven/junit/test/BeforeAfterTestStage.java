package com.tngtech.jgiven.junit.test;

import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ScenarioRule;

public class BeforeAfterTestStage {

    public int beforeCalled;
    public int afterCalled;
    public static int beforeScenarioCalled;
    public static int afterScenarioCalled;

    @ScenarioRule
    public RuleForTesting rule = new RuleForTesting();

    @BeforeStage
    public void before() {
        beforeCalled++;
    }

    @AfterStage
    public void after() {
        afterCalled++;
    }

    @BeforeScenario
    public void beforeScenario() {
        beforeScenarioCalled++;
    }

    @AfterScenario
    public void afterScenario() {
        afterScenarioCalled++;
    }

    public void something() {}

    public void someFailingStep() {
        throw new IllegalStateException( "failed step" );
    }

    public class RuleForTesting {
        public int afterCalled;
        public int beforeCalled;

        public void before() {
            this.beforeCalled++;
        }

        public void after() {
            this.afterCalled++;
        }
    }

}
