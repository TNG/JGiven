package com.tngtech.jgiven.impl;

import com.google.common.collect.Sets;

import com.tngtech.jgiven.annotation.NestedSteps;

import java.lang.reflect.Method;
import java.util.Set;


public class StackElement {

    private final Object receiver;
    private final Method method;
    private final Set<Class<?>> composedStages;

    public StackElement(Object receiver, Method method, Set<Class<?>> composedStages) {
        this.method = method;
        this.receiver = receiver;
        this.composedStages = composedStages;
    }

    public Object getReceiver() {
        return receiver;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isNestedMethod() {
        return method.isAnnotationPresent(NestedSteps.class);
    }

    public Set<Class<?>> getComposedStages() {
        return Sets.newHashSet(composedStages);
    }

}
