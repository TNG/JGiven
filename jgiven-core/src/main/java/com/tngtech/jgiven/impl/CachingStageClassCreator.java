package com.tngtech.jgiven.impl;

import net.bytebuddy.TypeCache;

/**
 * Caches stage classes
 */
public class CachingStageClassCreator implements StageClassCreator {

    private StageClassCreator stageCreator;
    private static TypeCache<Class<?>> typeCache = new TypeCache<>( TypeCache.Sort.SOFT );

    public CachingStageClassCreator( StageClassCreator stageCreator ){
        this.stageCreator = stageCreator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> createStageClass( Class<T> stageClass ){
        return (Class<? extends T>) typeCache.findOrInsert( stageClass.getClassLoader(), stageClass,
                () -> stageCreator.createStageClass( stageClass ) );
    }
}
