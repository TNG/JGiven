package com.tngtech.jgiven.tests.assumptions;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.tests.GivenTestStage;
import com.tngtech.jgiven.tests.JGivenReportExtractingExtension;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@SuppressWarnings("NewClassNamingConvention")
@ExtendWith(JGivenReportExtractingExtension.class)
public class JUnit5AssumptionTestScenarios {

    @ScenarioStage
    GivenTestStage givenTestStage;

    @SuppressWarnings("DataFlowIssue")
    @Test
    void test_with_failing_assumption() {
        Assumptions.assumeFalse(true);
    }

    @Test
    void test_with_failing_assumption_in_stage() {
        givenTestStage.given().a_failed_junit5_assumption();
    }

    @Test
    void test_with_failing_assumption_in_second_stage(){
        givenTestStage.given().nothing()
                .and().a_failed_junit5_assumption();
    }
}
