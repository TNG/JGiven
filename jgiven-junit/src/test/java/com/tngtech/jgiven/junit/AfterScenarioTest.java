package com.tngtech.jgiven.junit;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.junit.test.BeforeAfterTestStage;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;

@RunWith( DataProviderRunner.class )
public class AfterScenarioTest extends ScenarioTest<BeforeAfterTestStage, WhenTestStep, ThenTestStep> {

    @BeforeClass
    public static void beforeClass() {
        BeforeAfterTestStage.afterScenarioCalled = 0;
    }

    @DataProvider
    public static Object[][] testData() {
        return new Object[][] { { 0 }, { 1 }, { 2 } };
    }

    @Test
    @UseDataProvider( "testData" )
    public void testAfterScenario( int expectedCallCount ) {
        Assertions.assertThat( BeforeAfterTestStage.afterScenarioCalled ).isEqualTo( expectedCallCount );

        when().something();
    }
}
