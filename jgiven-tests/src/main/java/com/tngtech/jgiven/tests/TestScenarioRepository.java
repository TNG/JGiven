package com.tngtech.jgiven.tests;

import java.util.List;

import com.google.common.collect.Lists;

public class TestScenarioRepository {
    public static class SearchCriteria {
        public boolean notImplementedYet = false;
        public boolean failing = false;
        public Integer numberOfSteps;
        public Integer failingStep;
        public Boolean failIfPassed;
        public Boolean executeSteps;
        public Boolean tagAnnotation;

        public boolean matches( ScenarioCriteria criteria ) {
            if( notImplementedYet != criteria.notImplementedYet ) {
                return false;
            }

            if( failIfPassed != null && !failIfPassed.equals( criteria.failIfPassed ) ) {
                return false;
            }

            if( executeSteps != null && !executeSteps.equals( criteria.executeSteps ) ) {
                return false;
            }

            if( failing != criteria.failing ) {
                return false;
            }

            if( numberOfSteps != null && numberOfSteps != criteria.numberOfSteps ) {
                return false;
            }

            if( failingStep != null && !failingStep.equals( criteria.failingStep ) ) {
                return false;
            }

            if( tagAnnotation != null && !tagAnnotation.equals( criteria.tagAnnotation ) ) {
                return false;
            }

            return true;
        }
    }

    public static class ScenarioCriteria {
        public boolean notImplementedYet;
        public boolean failIfPassed;
        public boolean executeSteps;
        public boolean failing;
        public Integer failingStep;
        public int numberOfSteps = 1;
        public boolean tagAnnotation;

        public ScenarioCriteria notImplementedYet() {
            notImplementedYet = true;
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

        public ScenarioCriteria failingStep( int i ) {
            failing();
            failingStep = i;
            return this;
        }

        public ScenarioCriteria numberOfSteps( int n ) {
            numberOfSteps = n;
            return this;
        }

        public ScenarioCriteria tagAnnotation() {
            tagAnnotation = true;
            return this;
        }
    }

    public static class TestScenario {
        public Class<?> testClass = TestScenarios.class;
        public String testMethod;
        public ScenarioCriteria criteria = new ScenarioCriteria();

        public TestScenario( String testMethod ) {
            this.testMethod = testMethod;
        }

    }

    final static List<TestScenario> testScenarios = setupTestScenarios();

    public static TestScenario findScenario( SearchCriteria searchCriteria ) {
        for( TestScenario scenario : testScenarios ) {
            if( searchCriteria.matches( scenario.criteria ) ) {
                return scenario;
            }
        }
        throw new IllegalArgumentException( "No matching scenario found" );
    }

    private static ScenarioCriteria addTestScenario( List<TestScenario> list, String testMethod ) {
        TestScenario testScenario = new TestScenario( testMethod );
        list.add( testScenario );
        return testScenario.criteria;
    }

    private static List<TestScenario> setupTestScenarios() {
        List<TestScenario> result = Lists.newArrayList();

        addTestScenario( result, "failing_test_with_two_steps" )
            .numberOfSteps( 2 )
            .failingStep( 1 );

        addTestScenario( result, "failing_test_with_three_steps" )
            .numberOfSteps( 3 )
            .failingStep( 1 );

        addTestScenario( result, "failing_test_with_two_steps_and_second_step_fails" )
            .numberOfSteps( 2 )
            .failingStep( 2 );

        addTestScenario( result, "failing_test_with_NotImplementedYet_annotation" )
            .notImplementedYet()
            .numberOfSteps( 2 )
            .failingStep( 1 );

        addTestScenario( result, "passing_test_with_NotImplementedYet_annotation" )
            .notImplementedYet();

        addTestScenario( result, "passing_test_with_NotImplementedYet_annotation_and_failIfPassed_set_to_true" )
            .notImplementedYet()
            .failIfPassed();

        addTestScenario( result, "failing_test_with_NotImplementedYet_annotation_and_failIfPassed_set_to_true" )
            .notImplementedYet()
            .failIfPassed()
            .failingStep( 1 );

        addTestScenario( result, "failing_test_with_NotImplementedYet_annotation_and_executeSteps_set_to_true" )
            .notImplementedYet()
            .executeSteps()
            .failingStep( 1 );

        addTestScenario( result, "test_with_tag_annotation" )
            .tagAnnotation();

        return result;
    }

}
