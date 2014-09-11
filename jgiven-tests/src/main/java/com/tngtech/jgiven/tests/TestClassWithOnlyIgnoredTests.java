package com.tngtech.jgiven.tests;

import org.junit.Ignore;
import org.junit.Test;
import org.testng.annotations.Listeners;

import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.testng.ScenarioTestListener;

@Listeners( ScenarioTestListener.class )
public class TestClassWithOnlyIgnoredTests extends ScenarioTest<GivenTestStage, WhenTestStage, ThenTestStage> {

    @Ignore
    @Test
    @org.testng.annotations.Test( enabled = false )
    public void test() {

    }
}
