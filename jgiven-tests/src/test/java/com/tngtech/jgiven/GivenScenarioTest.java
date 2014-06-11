package com.tngtech.jgiven;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.tests.TestScenarioRepository;
import com.tngtech.jgiven.tests.TestScenarioRepository.SearchCriteria;
import com.tngtech.jgiven.tests.TestScenarioRepository.TestScenario;

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

    public SELF a_passing_test() {
        return self();
    }

    public SELF a_failing_test() {
        criteria.failing = true;
        return self();
    }

    public SELF the_test_is_annotated_with_NotImplementedYet() {
        criteria.notImplementedYet = true;
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

    public SELF the_test_has_a_tag_annotation_named( String name ) {
        assertThat( name ).isEqualTo( "TestTag" );
        criteria.tagAnnotation = true;
        return self();
    }

    @AfterStage
    public void findScenario() {
        testScenario = TestScenarioRepository.findScenario( criteria );
    }

    public SELF a_failing_test_with_$_steps( int n ) {
        a_failing_test();
        return a_test_with_$_steps( n );
    }

    public SELF a_test_with_$_steps( int n ) {
        criteria.numberOfSteps = n;
        return self();
    }

    public SELF step_$_fails( int i ) {
        criteria.failingStep = i;
        return self();
    }

}
