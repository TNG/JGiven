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
 * Sample Configuration:<br>
 *	{@literal @}Bean<br>
 *	{@literal @}Scope("prototype")<br>
 *  public SpringStepMethodInterceptor springStepMethodInterceptor() {<br>
 *	&nbsp;&nbsp;&nbsp;&nbsp;return new SpringStepMethodInterceptor();<br>
 *	}<br>
 * </code>
 * <p>
 * <strong>The StepMethodInterceptor is stateful, and thus should use "prototype" scope</strong>
 * @since 0.7.4
 */
public class SpringStepMethodInterceptor extends StepMethodInterceptor implements MethodInterceptor {

    public SpringStepMethodInterceptor() {
        super(null, null);
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Object receiver = invocation.getThis();
        Method method = invocation.getMethod();
        Object[] parameters = invocation.getArguments();
        Invoker invoker = new Invoker() {

            @Override
            public Object proceed() throws Throwable {
                return invocation.proceed();
            }
        };
        if (getScenarioMethodHandler() == null || getStackDepth() == null) {
            return invoker.proceed(); // not running in JGiven context
        }
        return doIntercept(receiver, method, parameters, invoker);
    }

}
