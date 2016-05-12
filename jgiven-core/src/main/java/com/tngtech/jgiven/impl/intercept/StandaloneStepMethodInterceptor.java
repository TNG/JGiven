package com.tngtech.jgiven.impl.intercept;

import com.tngtech.jgiven.impl.StackElement;

import java.lang.reflect.Method;
import java.util.Stack;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * StepMethodInterceptor that uses cglib {@link MethodInterceptor} for intercepting JGiven methods
 *
 */
public class StandaloneStepMethodInterceptor extends StepMethodInterceptor implements MethodInterceptor {

    public StandaloneStepMethodInterceptor(
        StepMethodHandler scenarioMethodHandler, Stack<StackElement> stack) {
        super(scenarioMethodHandler, stack);
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
