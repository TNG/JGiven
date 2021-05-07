package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks methods to be executed after a stage has been executed.
 * This essentially means that the method is executed
 * either on next call of a step method of the next stage,
 * or, for the last stage, after the scenario (but before any {@link AfterScenario} method).
 * <p>
 * If marked as repeatable, the {@code @AfterStage} method will execute every time a stage is reused;
 * if not marked as repeatable,it is guaranteed that the {@code @AfterStage} method is invoked only once.
 * <p>
 * {@code @AfterStage} can be used to finish builders, for example.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterStage {
    /**
     * marks an after stage method for repeated execution.
     */
    boolean repeatable() default false;
}
