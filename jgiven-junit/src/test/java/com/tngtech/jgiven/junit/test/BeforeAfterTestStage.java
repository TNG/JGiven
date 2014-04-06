package com.tngtech.jgiven.junit.test;

import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.BeforeStage;

public class BeforeAfterTestStage {

    public int beforeCalled;
    public int afterCalled;
    public static int beforeScenarioCalled;
    public static int afterScenarioCalled;

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
}
