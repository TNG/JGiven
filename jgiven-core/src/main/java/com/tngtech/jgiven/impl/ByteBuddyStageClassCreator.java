package com.tngtech.jgiven.impl;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * Creates JGiven stage subclasses at runtime using ByteBuddy.
 *
 * <p>The class loading strategy is chosen in three tiers, in order of
 * preference:</p>
 *
 * <ol>
 *   <li><strong>Reflection-based injection</strong> ({@link ClassLoadingStrategy.Default#INJECTION})
 *       &mdash; used when {@link ClassInjector.UsingReflection#isAvailable()} returns true.
 *       This is the default on Java 8 and on Java 9+ when
 *       {@code java.base/java.lang} is opened via
 *       {@code --add-opens java.base/java.lang=ALL-UNNAMED}. The generated
 *       subclass is injected into the same classloader as the stage class
 *       via {@code sun.misc.Unsafe}/{@code defineClass} reflection, which
 *       preserves package membership and is required for instrumenting
 *       package-private stage classes.</li>
 *
 *   <li><strong>Lookup-based injection</strong> ({@link ClassLoadingStrategy.UsingLookup})
 *       &mdash; used when reflective injection is unavailable but
 *       {@link ClassInjector.UsingLookup#isAvailable()} returns true and a
 *       {@link MethodHandles.Lookup} with {@code PACKAGE}/{@code MODULE}
 *       privileges over the stage class's package can be obtained via
 *       {@link MethodHandles#privateLookupIn}. The generated subclass is
 *       again injected into the stage class's classloader, preserving
 *       package membership. This tier is reached on Java 9+ runtimes that
 *       have not opened {@code java.base/java.lang} to reflection. If
 *       {@code privateLookupIn} fails (typically because the user's JPMS
 *       module does not {@code --add-opens} its package to JGiven), a
 *       single {@code WARN}-level log message is emitted and the strategy
 *       falls through to {@code WRAPPER}.</li>
 *
 *   <li><strong>Wrapping</strong> ({@link ClassLoadingStrategy.Default#WRAPPER})
 *       &mdash; fallback when neither injector is available, or when the
 *       lookup tier cannot acquire a usable {@code Lookup}. The generated
 *       subclass is placed in a separate child classloader. Package-private
 *       stages cannot be instrumented under {@code WRAPPER}; the user must
 *       either {@code --add-opens} their stage class's package or move the
 *       stage class to the unnamed module / classpath.</li>
 * </ol>
 *
 * <p>Subclasses may override {@link #getClassLoadingStrategy(Class)} to
 * take a different decision.</p>
 */
public class ByteBuddyStageClassCreator implements StageClassCreator {

    private static final Logger log = LoggerFactory.getLogger(ByteBuddyStageClassCreator.class);

    public static final String INTERCEPTOR_FIELD_NAME = "__jgiven_stepInterceptor";
    public static final String SETTER_NAME = "__jgiven_setStepInterceptor";

    /**
     * Attempts to build a {@link ClassLoadingStrategy.UsingLookup} that
     * injects the generated subclass into the stage class's classloader via
     * {@link MethodHandles.Lookup#defineClass}.
     *
     * <p>Returns {@code null} (allowing the caller to fall back to
     * {@link ClassLoadingStrategy.Default#WRAPPER}) when either the JVM does
     * not support lookup-based class definition or when
     * {@link MethodHandles#privateLookupIn} cannot acquire a lookup with
     * package/module privileges over the stage class's package &mdash;
     * typically because the user's JPMS module does not
     * {@code --add-opens} its package to JGiven. In the latter case a
     * single {@code WARN}-level message is logged so the user knows their
     * package-private stages will not be instrumentable.</p>
     */
    private static ClassLoadingStrategy<ClassLoader> tryLookupStrategy(Class<?> stageClass) {
        if (!ClassInjector.UsingLookup.isAvailable()) {
            return null;
        }
        try {
            // privateLookupIn gives PACKAGE/MODULE privs over the stage
            // class's package; required so Lookup#defineClass can place the
            // generated subclass in the same runtime package as its
            // superclass.
            Object lookup = MethodHandles.privateLookupIn(stageClass, MethodHandles.lookup());
            return ClassLoadingStrategy.UsingLookup.of(lookup);
        } catch (IllegalAccessException e) {
            log.warn(
                    "Could not acquire a MethodHandles.Lookup for stage class {} via privateLookupIn; "
                            + "falling back to WRAPPER class loading strategy. If this stage class is "
                            + "package-private or uses package-private members, open the stage class's "
                            + "module/package to JGiven via --add-opens {}=ALL-UNNAMED (or move the stage "
                            + "class to the unnamed module / classpath). Reason: {}",
                    stageClass.getName(),
                    stageClass.getModule().getName(),
                    e.toString());
            return null;
        } catch (Throwable t) {
            // Defensive: any other ByteBuddy/JVM oddity should not break the scenario.
            log.warn(
                    "Unexpected error while building UsingLookup class loading strategy for stage class {}; "
                            + "falling back to WRAPPER. Reason: {}",
                    stageClass.getName(),
                    t.toString());
            return null;
        }
    }

    public <T> Class<? extends T> createStageClass(Class<T> stageClass) {
        var instrumentation = new ByteBuddy()
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
        try (var instrumentedClass = instrumentation.make()) {
            return instrumentedClass
                    .load(getClassLoader(stageClass), getClassLoadingStrategy(stageClass))
                    .getLoaded();
        }
    }

    protected ClassLoadingStrategy<ClassLoader> getClassLoadingStrategy(Class<?> stageClass) {
        if (getClassLoader(stageClass) == null) {
            return ClassLoadingStrategy.Default.WRAPPER;
        }
        if (ClassInjector.UsingReflection.isAvailable()) {
            return ClassLoadingStrategy.Default.INJECTION;
        }
        ClassLoadingStrategy<ClassLoader> lookupStrategy = tryLookupStrategy(stageClass);
        return Objects.requireNonNullElse(lookupStrategy, ClassLoadingStrategy.Default.WRAPPER);
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
