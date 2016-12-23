package com.tngtech.jgiven.impl.inject;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState.Resolution;
import com.tngtech.jgiven.exception.AmbiguousResolutionException;
import com.tngtech.jgiven.exception.JGivenMissingRequiredScenarioStateException;
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

        for( ScenarioStateField field : getScenarioFields( object ) ) {
            field.getField().setAccessible( true );
            Resolution resolution = field.getResolution();
            Object key = null;
            if( resolution == Resolution.NAME ) {
                key = field.getField().getName();
            } else {
                key = field.getField().getType();
            }
            if( resolvedFields.containsKey( key ) ) {
                Field existingField = resolvedFields.get( key );
                throw new AmbiguousResolutionException( "Ambiguous fields with same " + resolution + " detected. Field 1: " +
                        existingField + ", field 2: " + field.getField() );
            }
            resolvedFields.put( key, field.getField() );
        }

        validatedClasses.put( object.getClass(), Boolean.TRUE );
    }

    private List<ScenarioStateField> getScenarioFields( Object object ) {
        @SuppressWarnings( "unchecked" )
        List<Field> scenarioFields = FieldCache
            .get( object.getClass() )
            .getFieldsWithAnnotation( ScenarioState.class, ProvidedScenarioState.class, ExpectedScenarioState.class );

        return Lists.transform( scenarioFields, ScenarioStateField.fromField );
    }

    @SuppressWarnings( "unchecked" )
    public void readValues( Object object ) {
        validateFields( object );
        for( ScenarioStateField field : getScenarioFields( object ) ) {
            try {
                Object value = field.getField().get( object );
                updateValue( field, value );
                log.debug( "Reading value {} from field {}", value, field.getField() );
            } catch( IllegalAccessException e ) {
                throw new RuntimeException( "Error while reading field " + field.getField(), e );
            }
        }
    }

    @SuppressWarnings( "unchecked" )
    public void updateValues( Object object ) {
        validateFields( object );
        for( ScenarioStateField field : getScenarioFields( object ) ) {
            Object value = getValue( field );

            if( value != null ) {
                try {
                    field.getField().set( object, value );
                } catch( IllegalAccessException e ) {
                    throw new RuntimeException( "Error while updating field " + field.getField(), e );
                }

                log.debug( "Setting field {} to value {}", field.getField(), value );
            } else if( field.isRequired() ) {
                throw new JGivenMissingRequiredScenarioStateException( field.getField() );
            }
        }
    }

    public <T> void injectValueByType( Class<T> clazz, T value ) {
        state.updateValueByType( clazz, value );
    }

    public <T> void injectValueByName( String name, T value ) {
        state.updateValueByName( name, value );
    }

    private void updateValue( ScenarioStateField field, Object value ) {
        if( field.getResolution() == Resolution.NAME ) {
            state.updateValueByName( field.getField().getName(), value );
        } else {
            state.updateValueByType( field.getField().getType(), value );
        }
    }

    private Object getValue( ScenarioStateField field ) {
        if( field.getResolution() == Resolution.NAME ) {
            return state.getValueByName( field.getField().getName() );
        }

        return state.getValueByType( field.getField().getType() );
    }

}
