package com.tngtech.jgiven.testng;

import org.assertj.core.api.Assertions;
import org.testng.ITestResult;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.junit.ThenReportModel;

public class ThenTestNGTest<SELF extends ThenTestNGTest<?>> extends ThenReportModel<SELF> {

    @ExpectedScenarioState
    ITestResult testResult;

    public SELF the_test_result_indicates_a_failure() {
        Assertions.assertThat( testResult.getStatus() ).isEqualTo( ITestResult.FAILURE );
        return self();
    }

}
