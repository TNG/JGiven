package com.tngtech.jgiven.integration.android;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.tngtech.jgiven.impl.ScenarioExecutor;
import com.tngtech.jgiven.impl.StandaloneScenarioExecutor;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.android.AndroidClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodDelegation;


import static net.bytebuddy.matcher.ElementMatchers.any;

/**
 * Created by originx on 10/17/2016.
 */
public class AndroidScenarioExecutor extends StandaloneScenarioExecutor implements ScenarioExecutor {

    Context context;

    public AndroidScenarioExecutor(Context context) {
        this.context = context;
    }

    @Override
    public <T> T createStageClass(Class<T> stepsClass) {
        try {
            methodInterceptor.enableMethodHandling(true);
            T result = new ByteBuddy()
                    .subclass(stepsClass, ConstructorStrategy.Default.IMITATE_SUPER_CLASS)
                    .method(any())
                    .intercept(MethodDelegation.to(methodInterceptor))
                    .make()
                    .load(context.getClassLoader(),
                            new  AndroidClassLoadingStrategy(ContextCompat.getCodeCacheDir(context)))
                    .getLoaded()
                    .newInstance();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error while trying to create an instance of class "+stepsClass, e);
        }
    }
}
