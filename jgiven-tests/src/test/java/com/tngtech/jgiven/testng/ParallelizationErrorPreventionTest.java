package com.tngtech.jgiven.testng;

import com.tngtech.jgiven.GivenScenarioTest;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.tags.FeatureTestNg;
import com.tngtech.jgiven.tags.Issue;
import com.tngtech.jgiven.testframework.ThenTestFramework;
import com.tngtech.jgiven.testframework.WhenTestFramework;
import org.junit.Test;

@FeatureTestNg
public class ParallelizationErrorPreventionTest extends
    JGivenScenarioTest<GivenScenarioTest<?>, WhenTestFramework<?>, ThenTestFramework<?>> {

    @Test
    @Issue("#829")
    public void attempting_to_run_testNG_in_parallel_with_injected_stages_is_prevented() {
        given().a_testNG_class_with_parallel_tests_and_injected_stages();
        when().the_test_is_executed_with_TestNG();
        then().the_test_fails_with_message("JGiven does not support using multi-threading and stage injection "
                + "in TestNG at the same time due to their different lifecycle models. "
                + "Please switch to single threaded execution or provide stages via inheriting from ScenarioTest. "
                + "This exception indicates that you used JGiven in a wrong way. Please consult the JGiven "
                + "documentation at http://jgiven.org/docs and the JGiven API documentation at "
                + "http://jgiven.org/javadoc/ for further information.");
    }
}
