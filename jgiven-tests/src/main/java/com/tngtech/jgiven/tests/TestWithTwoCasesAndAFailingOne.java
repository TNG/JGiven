package com.tngtech.jgiven.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.junit.ScenarioTest;

@RunWith( DataProviderRunner.class )
public class TestWithTwoCasesAndAFailingOne extends ScenarioTest<GivenTestStage, WhenTestStage, ThenTestStage> {

    @Test
    @DataProvider( { "true", "false" } )
    public void a_scenario_with_one_failing_case_leads_to_a_failed_scenario( boolean fail ) {
        given().a_failed_step( fail );
    }

}