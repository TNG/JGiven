package com.tngtech.jgiven.junit5;

import com.tngtech.jgiven.testframework.TestExecutionResult;
import com.tngtech.jgiven.tests.JGivenReportExtractingExtension;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;

class TestExecutionResultProvider implements TestExecutionListener {

    private final SourceFilter filterForRelevantSource;
    private TestExecutionResult executionResult;

    public TestExecutionResultProvider(SourceFilter filter) {
        this.filterForRelevantSource = filter;
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier,
                                  org.junit.platform.engine.TestExecutionResult testExecutionResult) {
        testIdentifier.getSource()
            .map(filterForRelevantSource)
            .flatMap(JGivenReportExtractingExtension::getReportModelFor)
            .map(jgivenReport -> new Junit5TestExecutionResult(jgivenReport, testExecutionResult))
            .ifPresent(this::setExecutionResult);
    }

    private void setExecutionResult(TestExecutionResult executionResult) {
        this.executionResult = executionResult;
    }

    public TestExecutionResult getExecutionResult() {
        if (executionResult != null) {
            return executionResult;
        } else {
            throw new IllegalStateException("Failed to obtain Test report for some reason.");
        }
    }

}
