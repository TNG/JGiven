package com.tngtech.jgiven.integration.spring;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.tngtech.jgiven.impl.intercept.StepInterceptor;
import com.tngtech.jgiven.impl.intercept.StepInterceptor.Invoker;

/**
 * StepInterceptorImpl that uses {@link MethodInterceptor} for intercepting JGiven methods
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
 * <strong>The StepInterceptorImpl is stateful, and thus should use "prototype" scope</strong>
 * @since 0.8.0
 */
public class SpringStepMethodInterceptor implements MethodInterceptor {

    private StepInterceptor stepInterceptor;

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
        return stepInterceptor.intercept( receiver, method, parameters, invoker );
    }

    public void setStepInterceptor(StepInterceptor stepInterceptor) {
        this.stepInterceptor = stepInterceptor;
    }
}
