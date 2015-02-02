package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.testframework.TestExecutionResult;
import org.junit.runner.Result;

public class JUnitExecutionResult extends TestExecutionResult {

    public Result result;

    @Override
    public int getFailureCount() {
        return result.getFailureCount();
    }

    @Override
    public String getFailureMessage( int i ) {
        return result.getFailures().get( i ).getMessage();
    }

}
