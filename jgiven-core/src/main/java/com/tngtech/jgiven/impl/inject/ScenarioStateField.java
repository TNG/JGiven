package com.tngtech.jgiven.impl.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.google.common.base.Function;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState.Resolution;

/**
 * Used internally to avoid repeated annotation lookups.
 */
final class ScenarioStateField {

    public static Function<Field, ScenarioStateField> fromField = new Function<Field, ScenarioStateField>() {
        @Override
        public ScenarioStateField apply( Field field ) {
            return new ScenarioStateField( field );
        }
    };

    private final Field field;

    private Resolution declaredResolution;
    private boolean required;

    private ScenarioStateField( Field field ) {
        this.field = field;

        collectAnnotations( field );
        if( declaredResolution == null ) {
            throw new IllegalArgumentException( "Field " + field + " has no valid annotation" );
        }
    }

    public Field getField() {
        return field;
    }

    /**
     * Return the {@link Resolution} defined for this state.
     */
    public Resolution getResolution() {
        if( declaredResolution == Resolution.AUTO ) {
            return typeIsTooGeneric( field.getType() ) ? Resolution.NAME : Resolution.TYPE;
        }

        return declaredResolution;
    }

    /**
     * Returns {@code true} if and only if the {@link Required} annotation is present on this state.
     */
    public boolean isRequired() {
        return required;
    }

    private void collectAnnotations( Field field ) {
        for( Annotation annotation : field.getAnnotations() ) {
            if( declaredResolution == null ) {
                declaredResolution = collectDeclaredResolution( annotation );
            }

            required |= collectRequired( annotation );
        }
    }

    private Resolution collectDeclaredResolution( Annotation annotation ) {
        if( annotation instanceof ScenarioState ) {
            return ( (ScenarioState) annotation ).resolution();
        }

        if( annotation instanceof ProvidedScenarioState ) {
            return ( (ProvidedScenarioState) annotation ).resolution();
        }

        if( annotation instanceof ExpectedScenarioState ) {
            return ( (ExpectedScenarioState) annotation ).resolution();
        }

        return null;
    }

    private boolean collectRequired( Annotation annotation ) {
        if( annotation instanceof ScenarioState ) {
            return ( (ScenarioState) annotation ).required();
        }

        if( annotation instanceof ExpectedScenarioState ) {
            return ( (ExpectedScenarioState) annotation ).required();
        }

        return false;
    }

    private boolean typeIsTooGeneric( Class<?> type ) {
        return type.isPrimitive()
                || type.getName().startsWith( "java.lang" )
                || type.getName().startsWith( "java.io" )
                || type.getName().startsWith( "java.util" );
    }

}
