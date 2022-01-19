package com.tngtech.jgiven.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


public class ConfigurationUtil {

    private static final LoadingCache<Class<?>, AbstractJGivenConfiguration> configurations =
        CacheBuilder.newBuilder().build(
            new CacheLoader<Class<?>, AbstractJGivenConfiguration>() {
                @Override
                public AbstractJGivenConfiguration load(Class<?> key) {
                    AbstractJGivenConfiguration result = (AbstractJGivenConfiguration) ReflectionUtil.newInstance(key);
                    result.configure();
                    return result;
                }
            });

    @SuppressWarnings({"unchecked"})
    public static <A extends AbstractJGivenConfiguration> AbstractJGivenConfiguration getConfiguration(
        Class<?> testClass) {
        Class<? extends AbstractJGivenConfiguration> configuration = Optional.ofNullable(testClass)
            .map(content -> content.getAnnotation(JGivenConfiguration.class))
            .map(content -> (Class<A>) content.value())
            .orElse((Class<A>) DefaultConfiguration.class);

        try {
            return configurations.get(configuration);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
