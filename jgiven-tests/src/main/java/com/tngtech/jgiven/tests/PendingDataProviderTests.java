package com.tngtech.jgiven.tests;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.annotation.Pending;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DataProviderRunner.class)
public class PendingDataProviderTests extends ScenarioTestForTesting<GivenTestStage, WhenTestStage, ThenTestStage> {

    @Test
    @Pending
    @DataProvider({"1", "2"})
    public void pending_scenario(int i) {
        given().nothing();
    }


}