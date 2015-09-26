package com.tngtech.jgiven.config;

import java.util.concurrent.ExecutionException;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.impl.util.ReflectionUtil;

public class ConfigurationUtil {

    private static final LoadingCache<Class<?>, AbstractJGivenConfiguration> configurations = CacheBuilder.newBuilder().build(
        new CacheLoader<Class<?>, AbstractJGivenConfiguration>() {
            @Override
            public AbstractJGivenConfiguration load( Class<?> key ) throws Exception {
                AbstractJGivenConfiguration result = (AbstractJGivenConfiguration) ReflectionUtil.newInstance( key );
                result.configure();
                return result;
            }
        } );

    public static AbstractJGivenConfiguration getConfiguration( Class<? extends Object> testClass ) {
        JGivenConfiguration annotation = testClass.getAnnotation( JGivenConfiguration.class );
        if( annotation == null ) {
            return new DefaultConfiguration();
        }

        try {
            return configurations.get( annotation.value() );
        } catch( ExecutionException e ) {
            throw Throwables.propagate( e.getCause() );
        }
    }
}
