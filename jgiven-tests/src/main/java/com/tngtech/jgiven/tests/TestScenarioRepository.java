package com.tngtech.jgiven.tests;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.annotation.Description;

import java.util.List;
import java.util.stream.Collectors;

public class TestScenarioRepository {


    public static class SearchCriteria {
        public boolean pending = false;
        public boolean failing = false;
        public Integer numberOfSteps;
        public Integer failingStep;
        public Integer numberOfFailingStages;
        public Boolean assumptionFailed;
        public Boolean failIfPassed;
        public Boolean executeSteps;
        public Boolean tagAnnotation;
        public Integer stageWithFailingAfterStageMethod;
        public Boolean parameterizedRunner;
        public Integer numberOfParameters;
        public String testClassDescription;

        public boolean matches(ScenarioCriteria criteria) {
            if(assumptionFailed != null && assumptionFailed != criteria.assumptionFailed){
                return false;
            }
            if (pending != criteria.pending) {
                return false;
            }

            if (failIfPassed != null && !failIfPassed.equals(criteria.failIfPassed)) {
                return false;
            }

            if (executeSteps != null && !executeSteps.equals(criteria.executeSteps)) {
                return false;
            }

            if (failing != criteria.failing) {
                return false;
            }

            if (numberOfSteps != null && !numberOfSteps.equals(criteria.numberOfSteps)) {
                return false;
            }

            if (numberOfFailingStages != null && !numberOfFailingStages.equals(criteria.numberOfFailingStages)) {
                return false;
            }

            if (failingStep != null && !failingStep.equals(criteria.failingStep)) {
                return false;
            }

            if (stageWithFailingAfterStageMethod != null
                && !stageWithFailingAfterStageMethod.equals(criteria.stageWithFailingAfterStageMethod)) {
                return false;
            }

            if (tagAnnotation != null && !tagAnnotation.equals(criteria.tagAnnotation)) {
                return false;
            }

            if (parameterizedRunner != null && !parameterizedRunner.equals(criteria.parameterizedRunner)) {
                return false;
            }

            if (numberOfParameters != null && !numberOfParameters.equals(criteria.numberOfParameters)) {
                return false;
            }

            if (testClassDescription != null && !testClassDescription.equals(criteria.testClassDescription)) {
                return false;
            }

            return true;
        }
    }

    public static class ScenarioCriteria {
        public boolean pending;
        public boolean failIfPassed;
        public boolean executeSteps;
        public boolean failing;
        public Integer failingStep;
        public boolean assumptionFailed;
        public int numberOfSteps = 1;
        public boolean tagAnnotation;
        private int numberOfFailingStages;
        public Integer stageWithFailingAfterStageMethod;
        public Integer numberOfParameters;
        private boolean parameterizedRunner;
        private String testClassDescription;

        public ScenarioCriteria pending() {
            pending = true;
            return this;
        }

        public ScenarioCriteria assumptionFailed() {
            assumptionFailed = true;
            return this;
        }

        public ScenarioCriteria failIfPassed() {
            failIfPassed = true;
            return this;
        }

        public ScenarioCriteria executeSteps() {
            executeSteps = true;
            return this;
        }

        public ScenarioCriteria failing() {
            failing = true;
            return this;
        }

        public ScenarioCriteria failingStep(int i) {
            failing();
            failingStep = i;
            return this;
        }

        public ScenarioCriteria numberOfSteps(int n) {
            numberOfSteps = n;
            return this;
        }

        public ScenarioCriteria tagAnnotation() {
            tagAnnotation = true;
            return this;
        }

        public ScenarioCriteria numberOfFailingStages(int i) {
            numberOfFailingStages = i;
            return this;
        }

        public ScenarioCriteria stageWithFailingAfterStageMethod(Integer stageWithFailingAfterStageMethod) {
            this.stageWithFailingAfterStageMethod = stageWithFailingAfterStageMethod;
            return this;
        }

        public ScenarioCriteria numberOfParameters(int n) {
            this.numberOfParameters = n;
            return this;
        }

        public ScenarioCriteria parameterizedRunner() {
            this.parameterizedRunner = true;
            return this;
        }

        public ScenarioCriteria testClassDescription(String value) {
            this.testClassDescription = value;
            return this;
        }
    }

    public static class TestScenario {
        public Class<?> testClass;
        public String testMethod;
        public ScenarioCriteria criteria = new ScenarioCriteria();

        public TestScenario(Class<?> testClass) {
            this.testClass = testClass;
        }

        public TestScenario(String testMethod) {
            this.testMethod = testMethod;
            this.testClass = TestScenarios.class;
        }

        public TestScenario(Class<?> testClass, String testMethod) {
            this.testClass = testClass;
            this.testMethod = testMethod;
        }
    }

    final static List<TestScenario> testScenarios = setupTestScenarios();

    public static List<TestScenario> findScenario(SearchCriteria searchCriteria) {
        return testScenarios.stream()
                .filter(s -> searchCriteria.matches(s.criteria))
                .collect(Collectors.toList());
    }

    private static ScenarioCriteria addTestScenario(List<TestScenario> list, Class<?> testClass) {
        TestScenario testScenario = new TestScenario(testClass);
        list.add(testScenario);
        return testScenario.criteria;
    }

    private static ScenarioCriteria addTestScenario(List<TestScenario> list, String testMethod) {
        TestScenario testScenario = new TestScenario(testMethod);
        list.add(testScenario);
        return testScenario.criteria;
    }

    private static ScenarioCriteria addTestScenario(List<TestScenario> list, Class<?> testClass, String testMethod) {
        TestScenario testScenario = new TestScenario(testClass);
        testScenario.testMethod = testMethod;
        list.add(testScenario);
        return testScenario.criteria;
    }

    private static List<TestScenario> setupTestScenarios() {
        List<TestScenario> result = Lists.newArrayList();

        addTestScenario(result, "failing_test_with_two_steps")
            .numberOfSteps(2)
            .failingStep(1);

        addTestScenario(result, "failing_test_with_three_steps")
            .numberOfSteps(3)
            .failingStep(1);

        addTestScenario(result, "failing_test_with_two_steps_and_second_step_fails")
            .numberOfSteps(2)
            .failingStep(2);

        addTestScenario(result, "failing_test_with_two_failing_stages")
            .numberOfSteps(2)
            .numberOfFailingStages(2)
            .failingStep(1);

        addTestScenario(result, "failing_test_where_second_stage_has_a_failing_after_stage_method")
            .numberOfSteps(2)
            .numberOfFailingStages(2)
            .stageWithFailingAfterStageMethod(2)
            .failingStep(1);

        addTestScenario(result, "failing_test_with_Pending_annotation")
            .pending()
            .numberOfSteps(2)
            .failingStep(1);

        addTestScenario(result, "passing_test_with_Pending_annotation")
            .pending();

        addTestScenario(result, "passing_test_with_Pending_annotation_and_failIfPassed_set_to_true")
            .pending()
            .failIfPassed();

        addTestScenario(result, "failing_test_with_Pending_annotation_and_failIfPassed_set_to_true")
            .pending()
            .failIfPassed()
            .failingStep(1);

        addTestScenario(result, "failing_test_with_Pending_annotation_and_executeSteps_set_to_true")
            .pending()
            .executeSteps()
            .failingStep(1);

        addTestScenario(result, "test_with_tag_annotation")
            .tagAnnotation();

        addTestScenario(result, TestClassWithParameterizedRunner.class)
            .parameterizedRunner()
            .numberOfParameters(2);

        addTestScenario(result, TestClassWithDescription.class, "some_test")
            .testClassDescription(TestClassWithDescription.class.getAnnotation(Description.class).value());

        return result;
    }

    public static TestScenario testClassWithOnlyIgnoredTests() {
        return new TestScenario(TestClassWithOnlyIgnoredTests.class);
    }

    public static TestScenario testClassWithAFailingScenarioAndAFailingAfterStage() {
        return new TestScenario(TestWithExceptionsInAfterMethod.class);
    }

    public static TestScenario testWithTwoCasesAndTheFirstOneFails() {
        return new TestScenario(TestWithTwoCasesAndAFailingOne.class);
    }

    public static TestScenario junit5TestsWithModificationsInAfterMethod() {
        return new TestScenario(JUnit5AfterMethodTests.class);
    }

    public static TestScenario testNgTestWithAFailingCase() {
        return new TestScenario(FailingCasesTestNgTest.class);
    }

    public static TestScenario junit5TestClassWithPerClassLifecycle() {
        return new TestScenario(TestWithPerClassLifecycle.class);
    }

    public static TestScenario testNgClassWithParallelTestsAndInjectedStages() {
        return new TestScenario(TestNgFailingParallelTest.class);
    }

    public static TestScenario lifecycleOrderingTest(){
        return new TestScenario(LifecycleOrderingTest.class);
    }

}
