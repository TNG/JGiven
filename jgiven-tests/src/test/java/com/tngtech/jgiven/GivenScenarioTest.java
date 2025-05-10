package com.tngtech.jgiven;

import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.tests.PendingDataProviderTests;
import com.tngtech.jgiven.tests.TestScenarioRepository;
import com.tngtech.jgiven.tests.TestScenarioRepository.SearchCriteria;
import com.tngtech.jgiven.tests.TestScenarioRepository.TestScenario;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Note that this is a rather unusual JGiven stage. Usually you would rather build up
 * your cases using some kind of builder. However, in this case this would be very complicated,
 * because it would require to build Java code dynamically, as JGiven scenarios are just plain Java code.
 * So instead of building the scenarios dynamically, the scenarios are predefined and selected dynamically
 * based on search criteria.
 */
public class GivenScenarioTest<SELF extends GivenScenarioTest<?>> extends Stage<SELF> {

    @ProvidedScenarioState
    TestScenario testScenario;

    SearchCriteria criteria = new SearchCriteria();

    public SELF a_test() {
        return self();
    }

    public SELF a_test_class() {
        return self();
    }

    public SELF a_passing_test() {
        return self();
    }

    public SELF a_failing_test() {
        criteria.failing = true;
        return self();
    }


    public SELF a_test_named_$(String testName, Class<?> testClass){
        testScenario = new TestScenario(testClass, testName);
        return self();
    }


    public SELF the_test_has_$_failing_stages(int n) {
        criteria.numberOfFailingStages = n;
        return self();
    }

    public SELF the_test_has_a_failed_assumption(){
        criteria.assumptionFailed = true;
        return self();
    }

    public SELF stage_$_has_a_failing_after_stage_method(int i) {
        criteria.stageWithFailingAfterStageMethod = i;
        return self();
    }

    public SELF the_test_is_annotated_with_Pending() {
        criteria.pending = true;
        return self();
    }

    public SELF failIfPassed_set_to_true() {
        criteria.failIfPassed = true;
        return self();
    }

    public SELF executeSteps_set_to_true() {
        criteria.executeSteps = true;
        return self();
    }

    public SELF the_test_has_a_tag_annotation_named(String name) {
        assertThat(name).isEqualTo("TestTag");
        criteria.tagAnnotation = true;
        return self();
    }

    @AfterStage
    public void findScenario() {
        if (testScenario == null) {
            testScenario = TestScenarioRepository.findScenario(criteria).stream().findFirst().orElseThrow();
        }
    }

    public SELF a_failing_test_with_$_steps(int n) {
        a_failing_test();
        return a_test_with_$_steps(n);
    }

    public SELF a_test_with_$_steps(int n) {
        criteria.numberOfSteps = n;
        return self();
    }

    public SELF step_$_fails(int i) {
        criteria.failingStep = i;
        return self();
    }

    public SELF the_test_class_has_a_description_annotation_with_value(String value) {
        criteria.testClassDescription = value;
        return self();
    }

    public SELF a_JUnit_test_class_with_the_Parameterized_Runner() {
        criteria.parameterizedRunner = true;
        return self();
    }

    public SELF the_test_class_has_$_parameters(int nParameters) {
        criteria.numberOfParameters = nParameters;
        return self();
    }

    public void a_test_class_with_all_tests_ignored() {
        testScenario = TestScenarioRepository.testClassWithOnlyIgnoredTests();
    }

    public void a_test_class_with_a_failing_scenario_and_a_failing_after_stage() {
        testScenario = TestScenarioRepository.testClassWithAFailingScenarioAndAFailingAfterStage();
    }

    public void a_test_with_two_cases_and_the_first_one_fails() {
        testScenario = TestScenarioRepository.testWithTwoCasesAndTheFirstOneFails();
    }

    public void a_TestNG_test_with_two_cases_and_the_first_one_fails() {
        testScenario = TestScenarioRepository.testNgTestWithAFailingCase();
    }

    public void a_pending_scenario_with_a_data_provider() {
        testScenario = new TestScenario(PendingDataProviderTests.class);
    }

    public SELF junit5_tests_with_scenario_modifications_in_after_method() {
        testScenario = TestScenarioRepository.junit5TestsWithModificationsInAfterMethod();
        return self();
    }

    public SELF junit5_test_class_with_a_per_class_lifecycle(){
        testScenario = TestScenarioRepository.junit5TestClassWithPerClassLifecycle();
        return self();
    }

    public SELF a_testNG_class_with_parallel_tests_and_injected_stages(){
        testScenario = TestScenarioRepository.testNgClassWithParallelTestsAndInjectedStages();
        return self();
    }

    public SELF a_test_with_framework_and_JGiven_before_and_after_methods(){
        testScenario = TestScenarioRepository.lifecycleOrderingTest();
        return self();
    }
}
