package com.tngtech.jgiven;

import com.tngtech.jgiven.ScenarioRuleTest.TestRule;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ScenarioRule;

public class BeforeAfterTestStage<SELF extends BeforeAfterTestStage<?>> extends Stage<SELF> {
    @ScenarioRule
    public TestRule rule = new TestRule();

    public int beforeCalled;
    public int afterCalled;
    public int afterStageCalled;

    public SELF something() {
        return self();
    }

    @AfterStage
    void someAfterStageMethod() {
        afterStageCalled++;
    }

    @BeforeScenario
    void someBeforeMethod() {
        beforeCalled++;
    }

    @AfterScenario
    void someAfterMethod() {
        afterCalled++;
    }
}