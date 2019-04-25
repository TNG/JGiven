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
     * This is similar to the {@link com.tngtech.jgiven.annotation.As} annotation,
     * but provides additional flexibility.
     *
     * @param name the new name of the step
     */
    void setName( String name );
}
