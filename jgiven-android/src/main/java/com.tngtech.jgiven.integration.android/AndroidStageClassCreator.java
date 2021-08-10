package com.tngtech.jgiven.integration.android;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import androidx.core.content.ContextCompat;
import com.tngtech.jgiven.impl.ByteBuddyStageClassCreator;
import net.bytebuddy.android.AndroidClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

public class AndroidStageClassCreator extends ByteBuddyStageClassCreator {

    @Override
    protected ClassLoader getClassLoader(Class<?> stageClass) {
        return getApplicationContext().getClassLoader();
    }

    @Override
    protected ClassLoadingStrategy getClassLoadingStrategy(Class<?> stageClass) {
        return new AndroidClassLoadingStrategy.Wrapping(ContextCompat.getCodeCacheDir(getApplicationContext()));
    }
}
