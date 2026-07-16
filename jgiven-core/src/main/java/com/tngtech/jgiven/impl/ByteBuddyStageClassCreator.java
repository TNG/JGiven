package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.exception.JGivenInjectionException;
import com.tngtech.jgiven.impl.intercept.ByteBuddyMethodInterceptor;
import com.tngtech.jgiven.impl.intercept.StageInterceptorInternal;
import com.tngtech.jgiven.impl.intercept.StepInterceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassInjector;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.FieldProxy;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.invoke.MethodHandles;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;

public class ByteBuddyStageClassCreator implements StageClassCreator {

    public static final String INTERCEPTOR_FIELD_NAME = "__jgiven_stepInterceptor";
    public static final String SETTER_NAME = "__jgiven_setStepInterceptor";

    public <T> Class<? extends T> createStageClass(Class<T> stageClass) {
        var byteBuddyConfig = new ByteBuddy()
                .subclass(stageClass, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING)
                .implement(StageInterceptorInternal.class)
                .defineField(INTERCEPTOR_FIELD_NAME, StepInterceptor.class)
                .method(named(SETTER_NAME))
                .intercept(
                        MethodDelegation.withDefaultConfiguration()
                                .withBinders(FieldProxy.Binder.install(
                                        StepInterceptorGetterSetter.class))
                                .to(new StepInterceptorSetter()))
                .method(not(named(SETTER_NAME)
                        .or(ElementMatchers.isDeclaredBy(Object.class))))
                .intercept(
                        MethodDelegation.withDefaultConfiguration()
                                .withBinders(FieldProxy.Binder.install(
                                        StepInterceptorGetterSetter.class))
                                .to(new ByteBuddyMethodInterceptor()));

        try (var madeByteBuddy = byteBuddyConfig.make()) {
            return madeByteBuddy
                    .load(getClassLoader(stageClass), getClassLoadingStrategy(stageClass))
                    .getLoaded();
        }
    }

    protected ClassLoadingStrategy<ClassLoader> getClassLoadingStrategy(Class<?> stageClass) {
        //case for core JDK classes which were loaded by the bootstrap class loader.
        if (getClassLoader(stageClass) == null) {
            return ClassLoadingStrategy.Default.WRAPPER;
        }
        return getInjectionStrategy(stageClass);
    }

    /**
     * Defines the generated subclass directly in the stage class's own class
     * loader and package, so that the subclass can access package-private
     * members of the stage class and so that the generated class is resolvable
     * by name from that class loader (e.g. by Spring, which references stage
     * beans by their generated class name). The reflection based
     * {@link ClassLoadingStrategy.Default#INJECTION} strategy is used where
     * available; on JVMs where it is not (e.g. Java 26+), the lookup based
     * {@link ClassLoadingStrategy.UsingLookup} strategy is used instead.
     */
    private ClassLoadingStrategy<ClassLoader> getInjectionStrategy(Class<?> stageClass) {
        if (ClassInjector.UsingReflection.isAvailable()) {
            return ClassLoadingStrategy.Default.INJECTION;
        } else if (ClassInjector.UsingLookup.isAvailable()) {
            try {
                MethodHandles.Lookup lookup =
                        MethodHandles.privateLookupIn(stageClass, MethodHandles.lookup());
                return ClassLoadingStrategy.UsingLookup.of(lookup);
            } catch (IllegalAccessException accessException) {
                throw new JGivenInjectionException("Failed to instrument stage class", accessException);
            }
        }
        throw new JGivenInjectionException("A stage class was provided that can be neither instrumented via injection, nor lookup, nor wrapping.", null);
    }

    protected ClassLoader getClassLoader(Class<?> stageClass) {
        return stageClass.getClassLoader();
    }

    public interface StepInterceptorGetterSetter {
        Object getValue();

        void setValue(Object value);
    }

    public static class StepInterceptorSetter {
        public void interceptSetter(StepInterceptor interceptor,
                                    @FieldProxy(INTERCEPTOR_FIELD_NAME) StepInterceptorGetterSetter stepInterceptorSetter) {
            stepInterceptorSetter.setValue(interceptor);
        }
    }
}
