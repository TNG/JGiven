package com.tngtech.jgiven.timing;

import com.tngtech.jgiven.impl.ScenarioBase;
import java.lang.instrument.Instrumentation;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Class that acts as a Java agent and injects code before class for timing the test methods.
 */
public class TimerInjectorAgent {
    /**
     * The method implementing the code injection done by Java Agents.
     */
    public static void premain(String args, Instrumentation instrumentation) {
        new AgentBuilder.Default()
                .type(ElementMatchers.isSubTypeOf(ScenarioBase.class))
                .transform((builder, type, classLoader, module) ->
                        builder.method(ElementMatchers.named("initialize"))
                                .intercept(MethodDelegation.to(ManageTimerInterceptor.class))
                ).installOn(instrumentation);
        new AgentBuilder.Default()
                .type(ElementMatchers.isSubTypeOf(ScenarioBase.class))
                .transform((builder, type, classLoader, module) ->
                        builder.method(ElementMatchers.named("finished"))
                                .intercept(MethodDelegation.to(ManageTimerInterceptor.class))
                ).installOn(instrumentation);
    }
}
