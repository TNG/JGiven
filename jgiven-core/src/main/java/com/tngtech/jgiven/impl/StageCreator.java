package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.impl.intercept.StepInterceptor;

/**
 * Creates instances of stage classes
 */
public interface StageCreator {

    <T> T createStage(Class<T> stageClass,  StepInterceptor stepInterceptor);
}
