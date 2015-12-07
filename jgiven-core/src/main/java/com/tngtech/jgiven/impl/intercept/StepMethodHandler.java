package com.tngtech.jgiven.impl.intercept;

import com.tngtech.jgiven.report.model.InvocationMode;

import java.lang.reflect.Method;

public interface StepMethodHandler {
    void handleMethod( Object targetObject, Method paramMethod, Object[] arguments, InvocationMode mode, Method parentMethod )
            throws Throwable;

    void handleThrowable( Throwable t ) throws Throwable;

    void handleMethodFinished( long durationInNanos );
}
