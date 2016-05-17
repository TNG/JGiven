package com.tngtech.jgiven.integration.spring;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.tngtech.jgiven.impl.intercept.StepMethodInterceptor;

/**
 * StepMethodInterceptor that uses {@link MethodInterceptor} for intercepting JGiven methods
 * See below on how to configure this bean.
 *
 * <p>
 * Sample Configuration:
 * <pre>
 * {@literal @}Bean
 * {@literal @}Scope("prototype")
 * public SpringStepMethodInterceptor springStepMethodInterceptor() {
 *     return new SpringStepMethodInterceptor();
 * }
 * </pre>
 * <p>
 * <strong>The StepMethodInterceptor is stateful, and thus should use "prototype" scope</strong>
 * @since 0.8.0
 */
public class SpringStepMethodInterceptor extends StepMethodInterceptor implements MethodInterceptor {

    public SpringStepMethodInterceptor() {
        super( null, null );
    }

    @Override
    public Object invoke( final MethodInvocation invocation ) throws Throwable {
        Object receiver = invocation.getThis();
        Method method = invocation.getMethod();
        Object[] parameters = invocation.getArguments();
        Invoker invoker = new Invoker() {

            @Override
            public Object proceed() throws Throwable {
                return invocation.proceed();
            }
        };
        if( getScenarioMethodHandler() == null ) {
            return invoker.proceed(); // not running in JGiven context
        }
        return doIntercept( receiver, method, parameters, invoker );
    }

}
