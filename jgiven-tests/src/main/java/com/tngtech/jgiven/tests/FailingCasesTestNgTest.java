package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.testng.ScenarioTestListener;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.tngtech.jgiven.testng.ScenarioTest;


@Listeners( ScenarioTestListener.class )
public class FailingCasesTestNgTest extends ScenarioTestForTesting<GivenTestStage, WhenTestStage, ThenTestStage> {

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
