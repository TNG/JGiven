package com.tngtech.jgiven.impl.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState.Resolution;
import com.tngtech.jgiven.exception.AmbiguousResolutionException;
import com.tngtech.jgiven.impl.util.FieldCache;

/**
 * Used by Scenario to inject and read values from objects.
 */
public class ValueInjector {
    private static final Logger log = LoggerFactory.getLogger( ValueInjector.class );

    private final ValueInjectorState state = new ValueInjectorState();

    /**
     * Caches all classes that have been already validated for ambiguous resolution.
     * This avoids duplicate validations of the same class.
     */
    private static final ConcurrentHashMap<Class<?>, Boolean> validatedClasses = new ConcurrentHashMap<Class<?>, Boolean>();

    /**
     * @throws AmbiguousResolutionException when multiple fields with the same resolution exist in the given object
     */
    @SuppressWarnings( "unchecked" )
    public void validateFields( Object object ) {
        if( validatedClasses.get( object.getClass() ) == Boolean.TRUE ) {
            return;
        }

        Map<Object, Field> resolvedFields = Maps.newHashMap();

        for( Field field : getScenarioFields( object ) ) {
            field.setAccessible( true );
            Resolution resolution = getResolution( field );
            Object key = null;
            if( resolution == Resolution.NAME ) {
                key = field.getName();
            } else {
                key = field.getType();
            }
            if( resolvedFields.containsKey( key ) ) {
                Field existingField = resolvedFields.get( key );
                throw new AmbiguousResolutionException( "Ambiguous fields with same " + resolution + " detected. Field 1: " +
                        existingField + ", field 2: " + field );
            }
            resolvedFields.put( key, field );
        }

        validatedClasses.put( object.getClass(), Boolean.TRUE );
    }

    private List<Field> getScenarioFields( Object object ) {
        return FieldCache.get( object.getClass() ).getFieldsWithAnnotation( ScenarioState.class, ProvidedScenarioState.class,
            ExpectedScenarioState.class );
    }

    @SuppressWarnings( "unchecked" )
    public void readValues( Object object ) {
        validateFields( object );
        for( Field field : getScenarioFields( object ) ) {
            try {
                Object value = field.get( object );
                updateValue( field, value );
                log.debug( "Reading value {} from field {}", value, field );
            } catch( IllegalAccessException e ) {
                throw new RuntimeException( "Error while reading field " + field, e );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    public void updateValues( Object object ) {
        validateFields( object );
        for( Field field : getScenarioFields( object ) ) {
            try {
                Object value = getValue( field );
                if( value != null ) {
                    field.set( object, value );
                    log.debug( "Setting field {} to value {}", field, value );
                }
            } catch( IllegalAccessException e ) {
                throw new RuntimeException( "Error while updating field " + field, e );
            }
        }
    }

    public <T> void injectValueByType( Class<T> clazz, T value ) {
        state.updateValueByType( clazz, value );
    }

    public <T> void injectValueByName( String name, T value ) {
        state.updateValueByName( name, value );
    }

    private void updateValue( Field field, Object value ) {
        Resolution resolution = getResolution( field );
        Class<?> type = field.getType();
        if( resolution == Resolution.NAME ) {
            String name = field.getName();
            state.updateValueByName( name, value );
        } else {
            state.updateValueByType( type, value );
        }
    }

    private Object getValue( Field field ) {
        Resolution resolution = getResolution( field );
        Class<?> type = field.getType();
        if( resolution == Resolution.NAME ) {
            String name = field.getName();
            return state.getValueByName( name );
        }
        return state.getValueByType( type );
    }

    private Resolution getResolution( Field field ) {
        Resolution resolution = getDeclaredResolution( field );
        if( resolution == Resolution.AUTO ) {
            return typeIsTooGeneric( field.getType() ) ? Resolution.NAME : Resolution.TYPE;
        }
        return resolution;
    }

    private Resolution getDeclaredResolution( Field field ) {
        for( Annotation annotation : field.getAnnotations() ) {
            if( annotation instanceof ScenarioState ) {
                return ( (ScenarioState) annotation ).resolution();
            }
            if( annotation instanceof ProvidedScenarioState ) {
                return ( (ProvidedScenarioState) annotation ).resolution();
            }
            if( annotation instanceof ExpectedScenarioState ) {
                return ( (ExpectedScenarioState) annotation ).resolution();
            }
        }
        throw new IllegalArgumentException( "Field " + field + " has no valid annotation" );
    }

    private boolean typeIsTooGeneric( Class<?> type ) {
        return type.isPrimitive()
                || type.getName().startsWith( "java.lang" )
                || type.getName().startsWith( "java.io" )
                || type.getName().startsWith( "java.util" );
    }

}
