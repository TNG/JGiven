package com.tngtech.jgiven;

import com.tngtech.jgiven.attachment.Attachment;

/**
 * This interface can be injected into a stage by using the {@link com.tngtech.jgiven.annotation.ScenarioState}
 * annotation. It provides programmatic access to the current executed step.

 * @since 0.7.0
 */
public interface CurrentStep {

    /**
     * Adds an attachment to the current step
     * 
     * @param attachment an attachment to add
     */
    void addAttachment( Attachment attachment );

    /**
     * Sets the extended description of the current step
     * 
     * @param extendedDescription the extended description
     * @see com.tngtech.jgiven.annotation.ExtendedDescription                           
     */
    void setExtendedDescription( String extendedDescription );

    /**
     * Sets the name of the current step.
     *
     * Note that in general it is better to use the {@link com.tngtech.jgiven.annotation.As} annotation,
     * to change the name of the step, because it can handle arguments in parametrized scenarios.
     * If you use this method in a parameterized scenario and the step varies within that scenario, JGiven
     * will not be able to create a data table.
     *
     * @param name the new name of the step
     */
    void setName( String name );

    /**
     * Sets the comment of the step.
     *
     * Not that the comment should be a constant string and not depending on arguments of the step, otherwise
     * the behavior of this method is undefined for parametrized scenarios.
     *
     * See {@link com.tngtech.jgiven.annotation.StepComment}
     *
     * @param comment the comment
     */
    void setComment( String comment );
}
