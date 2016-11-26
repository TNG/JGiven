package com.tngtech.jgiven.impl.intercept;

import java.lang.reflect.Method;

public interface StepInterceptor {

    /**
     * abstraction to continue intercepted method
     */
    interface Invoker {
        Object proceed() throws Throwable;
    };


    Object intercept(Object receiver, Method method, Object[] parameters, Invoker invoker ) throws Throwable;
}
