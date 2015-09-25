package com.tngtech.jgiven.tests;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.tngtech.jgiven.testng.ScenarioTest;

public class FailingCasesTestNgTest extends ScenarioTest<GivenTestStage, WhenTestStage, ThenTestStage> {

    @DataProvider
    public static Object[][] booleans() {
        return new Object[][] {
            { true },
            { false }
        };
    }

    @Test( dataProvider = "booleans" )
    public void failing_cases_do_not_lead_to_ignored_following_cases( boolean fail ) {
        given().a_failed_step( fail );
    }

}
