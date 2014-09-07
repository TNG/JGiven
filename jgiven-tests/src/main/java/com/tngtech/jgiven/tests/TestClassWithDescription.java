package com.tngtech.jgiven.tests;

import org.junit.Test;
import org.testng.annotations.Listeners;

import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.testng.ScenarioTestListener;

@Listeners( ScenarioTestListener.class )
@Description( "Test Description" )
public class TestClassWithDescription extends ScenarioTest<GivenTestStage, WhenTestStage, ThenTestStage> {

    @Test
    @org.testng.annotations.Test
    public void some_test() {
        given().nothing();
        when().something_happens();
        then().something_happened();
    }
}
