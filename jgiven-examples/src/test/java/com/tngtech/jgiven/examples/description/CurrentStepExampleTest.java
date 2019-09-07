package com.tngtech.jgiven.examples.description;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.tags.FeatureCurrentStep;

@RunWith( DataProviderRunner.class )
@Description( "Demonstrates the use of the CurrentStep interface" )
@FeatureCurrentStep
public class CurrentStepExampleTest extends SimpleScenarioTest<CurrentStepExampleTest.CurrentStepExampleStage> {

    @Test
    @ExtendedDescription( "This test shows how to use the CurrentStep interface to change the name of a step" )
    public void step_name_can_be_changed_with_CurrentStep() {
        given().step_name_changed_with_CurrentStep();
    }

    @Test
    public void setName_can_also_use_arguments() {
        given().step_name_changed_with_argument( "one argument" );
    }

    @Test
    @DataProvider( {
        "argument 1",
        "argument 2"
    } )
    @ExtendedDescription( "This test shows that setName also works with parametrized tests. " +
            "Note, however, that data tables cannot be created in this case. Use the @As annotation instead." )
    public void setName_with_arguments_also_works_with_parameterized_tests( String argument ) {
        given().step_name_changed_with_argument( argument );
    }

    static class CurrentStepExampleStage extends Stage<CurrentStepExampleStage> {

        @ScenarioState
        CurrentStep currentStep;

        @ExtendedDescription( "This step changes its name programmatically using the setStep method. " +
                "The name is actually step_name_changed_with_CurrentStep" )
        public CurrentStepExampleTest.CurrentStepExampleStage step_name_changed_with_CurrentStep() {
            this.currentStep.setName( "this step name is set with the CurrentStep interface" );
            return this;
        }

        public CurrentStepExampleTest.CurrentStepExampleStage step_name_changed_with_argument( String argument ) {
            this.currentStep.setName( "step " + argument );
            return this;
        }

    }

}
