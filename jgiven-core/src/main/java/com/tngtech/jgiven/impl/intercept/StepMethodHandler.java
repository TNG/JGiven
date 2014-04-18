package com.tngtech.jgiven.impl.intercept;

import java.lang.reflect.Method;

public interface StepMethodHandler {
    void handleMethod( Object targetObject, Method paramMethod, Object[] arguments ) throws Throwable;

    void handleThrowable( Throwable t ) throws Throwable;
}
