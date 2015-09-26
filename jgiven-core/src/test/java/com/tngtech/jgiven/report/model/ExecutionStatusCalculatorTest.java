package com.tngtech.jgiven.report.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ExecutionStatusCalculatorTest {

    @Test
    public void testExecutionStatusCalculation() {
        ExecutionStatusCalculator calculator = new ExecutionStatusCalculator();
        ScenarioModel model = new ScenarioModel();
        ScenarioCaseModel case1 = new ScenarioCaseModel();
        StepModel step1 = new StepModel();
        step1.setStatus( StepStatus.FAILED );
        case1.addStep( step1 );
        case1.setSuccess(false);
        model.addCase( case1 );
        model.accept( calculator );
        assertThat( calculator.executionStatus() ).isEqualTo( ExecutionStatus.FAILED );
    }

}
