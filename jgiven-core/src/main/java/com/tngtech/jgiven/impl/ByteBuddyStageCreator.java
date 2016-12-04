package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.impl.intercept.ByteBuddyMethodInterceptor;
import com.tngtech.jgiven.impl.intercept.StepInterceptor;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodDelegation;

import static net.bytebuddy.matcher.ElementMatchers.any;

public class ByteBuddyStageCreator implements StageCreator {

    @SuppressWarnings( "unchecked" )
    @Override
    public <T> T createStage(Class<T> stageClass, StepInterceptor stepInterceptor) {
        try {
            T result = new ByteBuddy()
                    .subclass(stageClass, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING)
                    .method(any())
                    .intercept(MethodDelegation.to(new ByteBuddyMethodInterceptor(stepInterceptor)))
                    .make()
                    .load(getClassLoader(stageClass),
                            getClassLoadingStrategy(stageClass))
                    .getLoaded()
                    .newInstance();
            return result;
        } catch (Error e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to create an instance of class "+stageClass, e);
        }
    }

    protected ClassLoadingStrategy getClassLoadingStrategy(Class<?> stageClass) {
        return getClassLoader(stageClass) == null
                ? ClassLoadingStrategy.Default.WRAPPER
                : ClassLoadingStrategy.Default.INJECTION;
    }

    protected ClassLoader getClassLoader(Class<?> stageClass) {
        return Thread.currentThread().getContextClassLoader();
    }


}
