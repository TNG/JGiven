package com.tngtech.jgiven.impl.inject;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Holds values based on their type or name.
 */
public class ValueInjectorState {
    private final Map<Class<?>, Object> valuesByType = Maps.newHashMap();
    private final Map<String, Object> valuesByName = Maps.newHashMap();

    public void updateValueByName( String name, Object value ) {
        valuesByName.put( name, value );
    }

    public void updateValueByType( Class<?> type, Object value ) {
        valuesByType.put( type, value );
    }

    public Object getValueByType( Class<?> type ) {
        return valuesByType.get( type );
    }

    public Object getValueByName( String name ) {
        return valuesByName.get( name );
    }

}
