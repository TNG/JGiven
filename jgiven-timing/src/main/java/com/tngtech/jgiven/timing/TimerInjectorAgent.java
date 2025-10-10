package com.tngtech.jgiven.timing;

import com.tngtech.jgiven.impl.ScenarioBase;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

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
                .transform((builder, type, classLoader, module, __) ->
                        builder.method(ElementMatchers.named("initialize")
                                .or(ElementMatchers.named("finished")))
                                .intercept(MethodDelegation.to(ManageTimerInterceptor.class))
                ).installOn(instrumentation);
    }
}
