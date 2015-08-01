package com.tngtech.jgiven.impl.intercept;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * StepMethodInterceptor that uses cglib {@link MethodInterceptor} for intercepting JGiven methods
 *
 */
public class StandaloneStepMethodInterceptor extends StepMethodInterceptor implements MethodInterceptor {

    public StandaloneStepMethodInterceptor(
            StepMethodHandler scenarioMethodHandler, AtomicInteger stackDepth) {
        super(scenarioMethodHandler, stackDepth);
    }

    @Override
    public Object intercept( final Object receiver, Method method, final Object[] parameters, final MethodProxy methodProxy ) throws Throwable {
        Invoker invoker = new Invoker() {

            @Override
            public Object proceed() throws Throwable {
                return methodProxy.invokeSuper( receiver, parameters );
            }

        };
        return doIntercept(receiver, method, parameters, invoker);
    }



}
