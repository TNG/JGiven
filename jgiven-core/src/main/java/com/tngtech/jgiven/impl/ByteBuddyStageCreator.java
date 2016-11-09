package com.tngtech.jgiven.impl;

import static com.tngtech.jgiven.relocated.bytebuddy.matcher.ElementMatchers.any;

import com.tngtech.jgiven.impl.intercept.ByteBuddyMethodInterceptor;
import com.tngtech.jgiven.impl.intercept.StageInterceptorInternal;
import com.tngtech.jgiven.impl.intercept.StepInterceptor;

import com.tngtech.jgiven.relocated.bytebuddy.ByteBuddy;
import com.tngtech.jgiven.relocated.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import com.tngtech.jgiven.relocated.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import com.tngtech.jgiven.relocated.bytebuddy.implementation.MethodDelegation;

public class ByteBuddyStageCreator implements StageCreator {

    @SuppressWarnings( "unchecked" )
    @Override
    public <T> T createStage( Class<T> stageClass, StepInterceptor stepInterceptor ) {
        try {
            T result = createStageClass( stageClass, stepInterceptor )
                .newInstance();
            return result;
        } catch( Error e ) {
            throw e;
        } catch( Exception e ) {
            throw new RuntimeException( "Error while trying to create an instance of class " + stageClass, e );
        }
    }

    public <T> Class<? extends T> createStageClass( Class<T> stageClass, StepInterceptor stepInterceptor ) {
        return new ByteBuddy()
            .subclass( stageClass, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING )
            .implement( StageInterceptorInternal.class )
            .method( any() )
            .intercept( MethodDelegation.to( new ByteBuddyMethodInterceptor( stepInterceptor ) ) )
            .make()
            .load( getClassLoader( stageClass ),
                getClassLoadingStrategy( stageClass ) )
            .getLoaded();
    }

    protected ClassLoadingStrategy getClassLoadingStrategy( Class<?> stageClass ) {
        return getClassLoader( stageClass ) == null
                ? ClassLoadingStrategy.Default.WRAPPER
                : ClassLoadingStrategy.Default.INJECTION;
    }

    protected ClassLoader getClassLoader( Class<?> stageClass ) {
        return Thread.currentThread().getContextClassLoader();
    }

}
