package com.tngtech.jgiven.impl.intercept;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import com.tngtech.jgiven.impl.ByteBuddyStageClassCreator;
import com.tngtech.jgiven.impl.ByteBuddyStageClassCreator.StepInterceptorGetterSetter;
import com.tngtech.jgiven.impl.intercept.StepInterceptor.Invoker;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.BindingPriority;
import net.bytebuddy.implementation.bind.annotation.DefaultCall;
import net.bytebuddy.implementation.bind.annotation.FieldProxy;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import static com.tngtech.jgiven.impl.ByteBuddyStageClassCreator.INTERCEPTOR_FIELD_NAME;

/**
 * StepInterceptorImpl that uses ByteBuddy Method interceptor with annotations for intercepting JGiven methods
 *
 */
public class ByteBuddyMethodInterceptor {

    @RuntimeType
    @BindingPriority( BindingPriority.DEFAULT * 3 )
    public Object interceptSuper( @SuperCall final Callable<?> zuper, @This final Object receiver, @Origin Method method,
            @AllArguments final Object[] parameters,
            @FieldProxy( INTERCEPTOR_FIELD_NAME ) StepInterceptorGetterSetter stepInterceptorGetter )
            throws Throwable {

        StepInterceptor interceptor = (StepInterceptor) stepInterceptorGetter.getValue();

        if( interceptor == null ) {
            return zuper.call();
        }

        Invoker invoker = new Invoker() {
            @Override
            public Object proceed() throws Throwable {
                return zuper.call();
            }

        };
        return interceptor.intercept( receiver, method, parameters, invoker );
    }

    @RuntimeType
    @BindingPriority( BindingPriority.DEFAULT * 2 )
    public Object interceptDefault( @DefaultCall final Callable<?> zuper, @This final Object receiver, @Origin Method method,
            @AllArguments final Object[] parameters,
            @FieldProxy( INTERCEPTOR_FIELD_NAME ) StepInterceptorGetterSetter stepInterceptorGetter )
            throws Throwable {

        StepInterceptor interceptor = (StepInterceptor) stepInterceptorGetter.getValue();

        if( interceptor == null ) {
            return zuper.call();
        }

        Invoker invoker = new Invoker() {
            @Override
            public Object proceed() throws Throwable {
                return zuper.call();
            }

        };
        return interceptor.intercept( receiver, method, parameters, invoker );
    }

    @RuntimeType
    public Object intercept( @This final Object receiver, @Origin final Method method,
                             @AllArguments final Object[] parameters,
                             @FieldProxy( INTERCEPTOR_FIELD_NAME ) StepInterceptorGetterSetter stepInterceptorGetter)
            throws Throwable {
        // this intercepted method does not have a non-abstract super method
        StepInterceptor interceptor = (StepInterceptor) stepInterceptorGetter.getValue();

        if( interceptor == null ) {
            return null;
        }

        Invoker invoker = new Invoker() {

            @Override
            public Object proceed() throws Throwable {
                return null;
            }

        };
        return interceptor.intercept( receiver, method, parameters, invoker );
    }

}
