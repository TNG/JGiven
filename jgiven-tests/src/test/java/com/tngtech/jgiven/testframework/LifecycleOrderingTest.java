package com.tngtech.jgiven.testframework;

import com.tngtech.jgiven.GivenScenarioTest;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.tags.FeatureJUnit;
import com.tngtech.jgiven.tags.FeatureJUnit5;
import com.tngtech.jgiven.tags.FeatureTestNg;
import org.junit.Test;

/**
 * This test backs the section "Integration into Test Frameworks' Lifecycles" of the documentation
 * Change the documentation if this test needs to be updated!
 */
public class LifecycleOrderingTest extends JGivenScenarioTest<
        GivenScenarioTest<?>, WhenTestFramework<?>, ThenLifecycleOrdering> {


    @Test
    @FeatureJUnit
    @Description("Ordering of before/after methods vs. before/after Scenario methods")
    public void junit_beforeAfter_beforeAfterScenario_execution_order() {
        given().a_test_with_framework_and_JGiven_before_and_after_methods();
        when().the_test_class_is_executed_with_JUnit();
        then().the_framework_before_method_was_executed_before_jgivens()
                .and()
                .the_framework_after_method_was_executed_before_jgivens();
    }

    @Test
    @FeatureJUnit5
    @Description("Ordering of before/after Each methods vs. before/after Scenario methods")
    public void junit5_beforeAfterEach_beforeAfterScenario_execution_order() {
       given().a_test_with_framework_and_JGiven_before_and_after_methods();
       when().the_test_class_is_executed_with_JUnit5();
       then().the_framework_before_method_was_executed_before_jgivens()
               .and().the_framework_after_method_was_executed_after_jgivens();
    }

    @Test
    @FeatureTestNg
    @Description("Ordering of before/after Test methods vs. before/after Scenario methods")
    public void testNg_beforeAfterTest_beforeAfterScenario_execution_order() {
        given().a_test_with_framework_and_JGiven_before_and_after_methods();
        when().the_test_class_is_executed_with(TestFramework.TestNG);
        then().the_framework_before_method_was_executed_before_jgivens()
                .and().the_framework_after_method_was_executed_after_jgivens();
    }
}
