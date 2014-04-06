package com.tngtech.jgiven.integration;

import java.lang.reflect.Field;

/**
 * Common interface for reflection-based field processors.
 * Implementations can be registered at the ScenarioExecutor
 * to implement custom behavior for fields in stages.
 * This is typically used to implement customer annotations.
 * <p>
 * The processor is executed once after the stage has been created,
 * but before values have been injected into the stage.
 * Thus a @ScenarioState annotation would override values 
 * that have been set by the processor.
 * <p>
 * As the implementation is based on reflection custom annotations must be declared
 * to be readable at runtime with @Retention( RetentionPolicy.RUNTIME )
 */
public interface StageFieldProcessor {

    /**
     * Process a concrete instance field.
     *  
     * @param stage the stage instance
     * @param field the field to process
     */
    public void process( Object stage, Field field ) throws Exception;
}
