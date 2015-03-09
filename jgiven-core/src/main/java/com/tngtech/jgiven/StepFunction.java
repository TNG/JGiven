package com.tngtech.jgiven;

/**
 * A functional interface for defining ad-hoc steps.
 *
 * @see Stage#$(String, StepFunction)
 * @param <STAGE> the stage in which this step is executed
 */
public interface StepFunction<STAGE> {

    public void apply( STAGE stage ) throws Exception;
}
