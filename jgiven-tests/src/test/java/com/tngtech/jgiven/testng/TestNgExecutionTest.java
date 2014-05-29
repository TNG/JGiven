package com.tngtech.jgiven.testng;

import org.junit.Test;

import com.tngtech.jgiven.GivenScenarioTest;
import com.tngtech.jgiven.tags.FeatureTestNg;

@FeatureTestNg
public class TestNgExecutionTest extends com.tngtech.jgiven.junit.ScenarioTest<GivenScenarioTest<?>, WhenTestNGTest<?>, ThenTestNGTest<?>> {

    @Test
    public void the_error_message_of_a_failing_step_is_reported() {
        given().a_failing_test();
        when().the_test_is_executed_with_TestNG();
        then().the_test_result_indicates_a_failure()
            .and().an_error_message_is_stored_in_the_report();
    }
}
