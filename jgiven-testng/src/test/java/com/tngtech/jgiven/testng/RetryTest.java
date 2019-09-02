package com.tngtech.jgiven.testng;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.annotations.Test;

@Test
public class RetryTest extends SimpleScenarioTest<TestNgTest.TestSteps> {

    int count = 0;

    @Test(retryAnalyzer = MyAnalyzer.class)
    public void failing_with_retry_test() {
        when().something_should_$_fail(count++ == 0);
    }

    public static class MyAnalyzer implements IRetryAnalyzer {
        int count = 0;
        @Override
        public boolean retry(ITestResult result) {
            return count++ == 0;
        }
    }
}
