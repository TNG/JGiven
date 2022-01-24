package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.testng.ScenarioTestListener;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.testng.annotations.Listeners;

@Listeners( ScenarioTestListener.class )
public class TestClassWithOnlyIgnoredTests extends ScenarioTestForTesting<GivenTestStage, WhenTestStage, ThenTestStage> {

    @Ignore
    @Test
    @org.junit.jupiter.api.Test
    @Disabled
    @org.testng.annotations.Test( enabled = false )
    public void test() {

    }
}
