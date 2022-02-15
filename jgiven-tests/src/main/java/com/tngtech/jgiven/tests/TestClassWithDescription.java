package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.testng.ScenarioTestListener;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testng.annotations.Listeners;

@Listeners( ScenarioTestListener.class )
@ExtendWith(JGivenReportExtractingExtension.class)
@Description( "Test Description" )
public class TestClassWithDescription extends ScenarioTest<GivenTestStage, WhenTestStage, ThenTestStage> {

    @Test
    @org.junit.jupiter.api.Test
    @org.testng.annotations.Test
    public void some_test() {
        given().nothing();
        when().something_happens();
        then().something_happened();
    }
}
