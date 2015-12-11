package com.tngtech.jgiven.impl.intercept;

import java.lang.reflect.Method;

import com.tngtech.jgiven.report.model.InvocationMode;

public interface StepMethodHandler {
    void handleMethod( Object targetObject, Method paramMethod, Object[] arguments, InvocationMode mode, boolean hasNestedSteps )
            throws Throwable;

    void handleThrowable( Throwable t ) throws Throwable;

    void handleMethodFinished( long durationInNanos, boolean hasNestedSteps );
}
