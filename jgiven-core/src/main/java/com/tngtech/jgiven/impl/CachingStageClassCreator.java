package com.tngtech.jgiven.impl;

import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caches stage classes
 */
public class CachingStageClassCreator implements StageClassCreator {

    private StageClassCreator stageCreator;

    private final static ConcurrentHashMap<Class<?>, Class<?>> createdStages = new ConcurrentHashMap<Class<?>, Class<?>>( 50 );

    public CachingStageClassCreator( StageClassCreator stageCreator ) {
        this.stageCreator = stageCreator;
    }

    @Override
    public <T> Class<? extends T> createStageClass( Class<T> stageClass ) {
        return getCachedClass(stageClass, stageCreator);
    }

    private static <T> Class<? extends T> getCachedClass(Class<T> stageClass, StageClassCreator stageCreator) {
        Class<?> stageSubClass = createdStages.get( stageClass );
        if( stageSubClass == null ) {
            synchronized (createdStages) {
                stageSubClass = stageCreator.createStageClass(stageClass);
                createdStages.put(stageClass, stageSubClass);
            }
        }

        return (Class<? extends T>) stageSubClass;
    }
}
