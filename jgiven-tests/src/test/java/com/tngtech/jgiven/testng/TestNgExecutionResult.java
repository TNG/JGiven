package com.tngtech.jgiven.testng;

import java.util.List;

import org.testng.ITestResult;

import com.tngtech.jgiven.relocated.guava.base.Predicate;
import com.tngtech.jgiven.relocated.guava.collect.FluentIterable;
import com.tngtech.jgiven.testframework.TestExecutionResult;

public class TestNgExecutionResult extends TestExecutionResult {

    public List<ITestResult> testResults;

    @Override
    public int getFailureCount() {
        return FluentIterable.from( testResults ).filter( new Predicate<ITestResult>() {
            @Override
            public boolean apply( ITestResult input ) {
                return input.getStatus() == ITestResult.FAILURE;
            }
        } ).size();
    }

    @Override
    public String getFailureMessage( int i ) {
        return testResults.get( i ).getThrowable().getMessage();
    }
}
