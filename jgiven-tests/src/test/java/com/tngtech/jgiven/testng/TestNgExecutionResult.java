package com.tngtech.jgiven.testng;

import com.tngtech.jgiven.testframework.TestExecutionResult;
import org.testng.ITestResult;

public class TestNgExecutionResult extends TestExecutionResult {

    public ITestResult testResult;

    @Override
    public int getFailureCount() {
        return testResult.getStatus() == ITestResult.FAILURE ? 1 : 0;
    }

    @Override
    public String getFailureMessage( int i ) {
        return testResult.getThrowable().getMessage();
    }
}
