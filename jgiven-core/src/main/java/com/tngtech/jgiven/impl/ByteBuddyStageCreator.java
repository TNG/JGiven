package com.tngtech.jgiven.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.tngtech.jgiven.impl.intercept.ByteBuddyMethodInterceptor;
import com.tngtech.jgiven.impl.intercept.StageInterceptorInternal;
import com.tngtech.jgiven.impl.intercept.StepInterceptor;
import com.tngtech.jgiven.impl.util.ReflectionUtil;

public class ByteBuddyStageCreator implements StageCreator {

    private final StageClassCreator stageClassCreator;

    public ByteBuddyStageCreator(StageClassCreator stageClassCreator) {
        this.stageClassCreator = stageClassCreator;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <T> T createStage( Class<T> stageClass, StepInterceptor stepInterceptor ) {
        try {
            Class<? extends T> stageSubClass = stageClassCreator.createStageClass(stageClass);
            T result = stageSubClass.newInstance();
            setStepInterceptor( result, stepInterceptor);
            return result;
        } catch( Error e ) {
            throw e;
        } catch( Exception e ) {
            throw new RuntimeException( "Error while trying to create an instance of class " + stageClass, e );
        }
    }

    protected <T> void setStepInterceptor(T result, StepInterceptor stepInterceptor) {
        ((StageInterceptorInternal) result).__jgiven_setStepInterceptor(stepInterceptor);
    }

}
