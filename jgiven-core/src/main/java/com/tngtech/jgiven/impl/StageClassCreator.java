package com.tngtech.jgiven.impl;

/**
 * Responsible for creating instrumented sub-classes of the given {@code stageClass}.
 * The sub-class intercepts all methods calls and delegates them to
 * an instance of {@link com.tngtech.jgiven.impl.intercept.StepInterceptor}
 */
public interface StageClassCreator {
    <T> Class<? extends T> createStageClass( Class<T> stageClass );
}
