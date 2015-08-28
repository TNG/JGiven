package com.tngtech.jgiven.tests.java8;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.report.model.StepModel;

public class Java8Test extends SimpleScenarioTest<LambdaSteps<?>> {

    @Test
    public void lambda_steps_work() {

        given().some_lambda_step( 5, 4 );

        StepModel step = getScenario().getScenarioCaseModel().getFirstStep();
        assertThat( step.getWord( 2 ).getArgumentInfo().getArgumentName() ).isEqualTo( "a" );
        assertThat( step.getWord( 3 ).getArgumentInfo().getArgumentName() ).isEqualTo( "b" );

    }

}
