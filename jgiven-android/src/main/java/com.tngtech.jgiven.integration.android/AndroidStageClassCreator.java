package com.tngtech.jgiven.integration.android;

import android.support.test.InstrumentationRegistry;
import android.support.v4.content.ContextCompat;

import com.tngtech.jgiven.impl.ByteBuddyStageClassCreator;

import net.bytebuddy.android.AndroidClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

public class AndroidStageClassCreator extends ByteBuddyStageClassCreator {

    @Override
    protected ClassLoader getClassLoader(Class<?> stageClass) {
        return InstrumentationRegistry.getTargetContext().getClassLoader();
    }

    @Override
    protected ClassLoadingStrategy getClassLoadingStrategy(Class<?> stageClass) {
        return new AndroidClassLoadingStrategy.Wrapping(ContextCompat.getCodeCacheDir(InstrumentationRegistry.getTargetContext()));
    }
}
