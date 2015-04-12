package com.tngtech.jgiven.impl.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Cache to avoid multiple expensive reflection-based look-ups
 * @since 0.7.1
 */
public class FieldCache {

    private static final ConcurrentHashMap<Class<?>, FieldCache> instances = new ConcurrentHashMap<Class<?>, FieldCache>();

    public static FieldCache get( Class<?> clazz ) {
        FieldCache fieldCache = instances.get( clazz );

        if( fieldCache == null ) {
            fieldCache = new FieldCache( clazz );
            instances.put( clazz, fieldCache );
        }

        return fieldCache;
    }

    private final Class<?> clazz;
    private final ConcurrentHashMap<List<Class<? extends Annotation>>, List<Field>> fieldMap = new ConcurrentHashMap<List<Class<? extends Annotation>>, List<Field>>();

    public FieldCache( Class<?> clazz ) {
        this.clazz = clazz;
    }

    public List<Field> getFieldsWithAnnotation( final Class<? extends Annotation>... scenarioStageClasses ) {
        List<Class<? extends Annotation>> annotationList = ImmutableList.copyOf( scenarioStageClasses );
        List<Field> fields = fieldMap.get( annotationList );
        if( fields == null ) {
            final List<Field> newFields = Lists.newArrayList();
            ReflectionUtil.forEachField( null, clazz,
                ReflectionUtil.hasAtLeastOneAnnotation( scenarioStageClasses ),
                new ReflectionUtil.FieldAction() {
                    @Override
                    public void act( Object object, Field field ) throws Exception {
                        field.setAccessible( true );
                        newFields.add( field );
                    }
                }
                );
            fieldMap.put( annotationList, newFields );
            return newFields;
        }
        return fields;
    }
}
