package com.tngtech.jgiven.impl;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;

import com.tngtech.jgiven.base.StageName;
import com.tngtech.jgiven.impl.intercept.ByteBuddyMethodInterceptor;
import com.tngtech.jgiven.impl.intercept.StageInterceptorInternal;
import com.tngtech.jgiven.impl.intercept.StageNameInternal;
import com.tngtech.jgiven.impl.intercept.StepInterceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.FieldProxy;
import net.bytebuddy.matcher.ElementMatchers;

public class ByteBuddyStageClassCreator implements StageClassCreator {

    public static final String INTERCEPTOR_FIELD_NAME = "__jgiven_stepInterceptor";
    public static final String STAGE_NAME_WRAPPER_FIELD_NAME = "__jgiven_stageNameWrapper";
    public static final String SETTER_NAME = "__jgiven_setStepInterceptor";
    public static final String STAGE_NAME_WRAPPER_GETTER = "__jgiven_getStageNameWrapper";
    public static final String STAGE_NAME_WRAPPER_SETTER = "__jgiven_setStageNameWrapper";

    public <T> Class<? extends T> createStageClass(Class<T> stageClass) {
        return new ByteBuddy()
                .subclass(stageClass, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING)
                .implement(StageInterceptorInternal.class, StageNameInternal.class)
                .defineField(INTERCEPTOR_FIELD_NAME, StepInterceptor.class)
                .defineField(STAGE_NAME_WRAPPER_FIELD_NAME, StageName.class)
                .method(named(SETTER_NAME))
                .intercept(
                        MethodDelegation.withDefaultConfiguration()
                                .withBinders(FieldProxy.Binder.install(
                                        StepInterceptorGetterSetter.class))
                                .to(new StepInterceptorSetter()))
                .method(named(STAGE_NAME_WRAPPER_GETTER).or(named(STAGE_NAME_WRAPPER_SETTER)))
                .intercept(
                        MethodDelegation.withDefaultConfiguration()
                                .withBinders(FieldProxy.Binder.install(
                                        StageNameWrapperGetterSetter.class))
                                .to(new StageNameWrapperInterceptor()))
                .method(not(named(SETTER_NAME)
                            .or(named(STAGE_NAME_WRAPPER_GETTER))
                            .or(named(STAGE_NAME_WRAPPER_SETTER))
                        .or(ElementMatchers.isDeclaredBy(Object.class))))
                .intercept(
                        MethodDelegation.withDefaultConfiguration()
                                .withBinders(FieldProxy.Binder.install(
                                        StepInterceptorGetterSetter.class))
                                .to(new ByteBuddyMethodInterceptor()))
                .make()
                .load(getClassLoader(stageClass),
                        getClassLoadingStrategy(stageClass))
                .getLoaded();
    }

    protected ClassLoadingStrategy getClassLoadingStrategy(Class<?> stageClass) {
        return getClassLoader(stageClass) == null
                ? ClassLoadingStrategy.Default.WRAPPER
                : ClassLoadingStrategy.Default.INJECTION;
    }

    protected ClassLoader getClassLoader(Class<?> stageClass) {
        return stageClass.getClassLoader();
    }

    public interface StageNameWrapperGetterSetter {
        Object getValue();

        void setValue(Object value);
    }

    public static class StageNameWrapperInterceptor {
        public StageName interceptGetter(@FieldProxy(STAGE_NAME_WRAPPER_FIELD_NAME) StageNameWrapperGetterSetter stageNameWrapperGetter) {
            return (StageName) stageNameWrapperGetter.getValue();
        }

        public void interceptSetter(StageName interceptor,
                                    @FieldProxy(STAGE_NAME_WRAPPER_FIELD_NAME) StageNameWrapperGetterSetter stageNameWrapperSetter) {
            stageNameWrapperSetter.setValue(interceptor);
        }
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
