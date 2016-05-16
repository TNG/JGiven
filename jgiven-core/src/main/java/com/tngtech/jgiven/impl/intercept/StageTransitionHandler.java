package com.tngtech.jgiven.impl.intercept;

/**
 * Responsible for handling stage transitions
 */
public interface StageTransitionHandler {

    /**
     * Called when childStage is entered.
     * 
     * @param parentStage is {@code null} if no parent stage exists
     * @param childStage the stage to enter
     * @throws Throwable
     */
    void enterStage( Object parentStage, Object childStage ) throws Throwable;

    /**
     * Called when the child stage has been left and the
     * parent stage is reentered.
     * 
     * @param parentStage is {@code null} if no parent stage exist
     * @param childStage the stage that is left
     */
    void leaveStage( Object parentStage, Object childStage ) throws Throwable;
}
