package com.tngtech.jgiven.impl;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;

import com.tngtech.jgiven.impl.intercept.ByteBuddyMethodInterceptor;
import com.tngtech.jgiven.impl.intercept.StageInterceptorInternal;
import com.tngtech.jgiven.impl.intercept.StepInterceptor;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import net.bytebuddy.implementation.bind.annotation.FieldProxy;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;

public class ByteBuddyStageClassCreator implements StageClassCreator {

    public static final String INTERCEPTOR_FIELD_NAME = "__jgiven_stepInterceptor";
    public static final String SETTER_NAME = "__jgiven_setStepInterceptor";

    public interface StepInterceptorGetterSetter {
        Object getValue();
        void setValue(Object value);
    }

    public static class StepInterceptorSetter {
        public void interceptSetter(StepInterceptor interceptor,
                                    @FieldProxy( INTERCEPTOR_FIELD_NAME ) StepInterceptorGetterSetter stepInterceptorSetter) {
            stepInterceptorSetter.setValue(interceptor);
        }
    }

    public <T> Class<? extends T> createStageClass( Class<T> stageClass ) {
        return new ByteBuddy()
            .subclass( stageClass, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING )
            .implement( StageInterceptorInternal.class )
            .defineField( INTERCEPTOR_FIELD_NAME, StepInterceptor.class )
            .method( named(SETTER_NAME) )
                .intercept(
                    MethodDelegation.withDefaultConfiguration()
                        .withBinders( FieldProxy.Binder.install(
                                StepInterceptorGetterSetter.class ))
                .to(new StepInterceptorSetter() ))
            .method( not( named( SETTER_NAME )
                    .or(ElementMatchers.isDeclaredBy(Object.class))))
            .intercept(
                    MethodDelegation.withDefaultConfiguration()
                    .withBinders(FieldProxy.Binder.install(
                            StepInterceptorGetterSetter.class ))
                .to( new ByteBuddyMethodInterceptor() ))
            .make()
            .load( stageClass.getClassLoader(),
                getClassLoadingStrategy( stageClass ) )
            .getLoaded();
    }

    protected ClassLoadingStrategy getClassLoadingStrategy( Class<?> stageClass ) {
        return stageClass.getClassLoader() == null
                ? ClassLoadingStrategy.Default.WRAPPER
                : ClassLoadingStrategy.Default.INJECTION;
    }
}
