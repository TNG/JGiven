package com.tngtech.jgiven.impl.inject;

import static java.util.stream.Collectors.toList;

import com.google.common.collect.Maps;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState.Resolution;
import com.tngtech.jgiven.exception.AmbiguousResolutionException;
import com.tngtech.jgiven.exception.JGivenMissingGuaranteedScenarioStateException;
import com.tngtech.jgiven.exception.JGivenMissingRequiredScenarioStateException;
import com.tngtech.jgiven.impl.util.FieldCache;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used by Scenario to inject and read values from objects.
 */
public class ValueInjector {
    private static final Logger log = LoggerFactory.getLogger(ValueInjector.class);
    /**
     * Caches all classes that have been already validated for ambiguous resolution.
     * This avoids duplicate validations of the same class.
     */
    private static final ConcurrentHashMap<Class<?>, Boolean> validatedClasses = new ConcurrentHashMap<>();
    private final ValueInjectorState state = new ValueInjectorState();

    /**
     * @throws AmbiguousResolutionException when multiple fields with the same resolution exist in the given object
     */
    @SuppressWarnings("unchecked")
    public void validateFields(Object object) {
        if (validatedClasses.get(object.getClass()) == Boolean.TRUE) {
            return;
        }

        Map<Object, Field> resolvedFields = Maps.newHashMap();

        for (ScenarioStateField field : getScenarioFields(object)) {
            field.getField().setAccessible(true);
            Resolution resolution = field.getResolution();
            Object key = null;
            if (resolution == Resolution.NAME) {
                key = field.getField().getName();
            } else {
                key = field.getField().getType();
            }
            if (resolvedFields.containsKey(key)) {
                Field existingField = resolvedFields.get(key);
                throw new AmbiguousResolutionException("Ambiguous fields with same " + resolution + " detected. Field 1: "
                        + existingField + ", field 2: " + field.getField());
            }
            resolvedFields.put(key, field.getField());
        }

        validatedClasses.put(object.getClass(), Boolean.TRUE);
    }

    private List<ScenarioStateField> getScenarioFields(Object object) {
        @SuppressWarnings("unchecked")
        List<Field> scenarioFields = FieldCache
                .get(object.getClass())
                .getFieldsWithAnnotation(ScenarioState.class, ProvidedScenarioState.class, ExpectedScenarioState.class);

        return scenarioFields.stream()
                .map(ScenarioStateField.fromField)
                .collect(toList());
    }

    /**
     * @throws JGivenMissingGuaranteedScenarioStateException in case a field is guaranteed
     *                                                     and is not initialized by the finishing stage
     */
    @SuppressWarnings("unchecked")
    public void readValues(Object object) {
        validateFields(object);
        checkGuaranteedStatesAreInitialized(object);

        for (ScenarioStateField field : getScenarioFields(object)) {
            try {
                Object value = field.getField().get(object);
                updateValue(field, value);
                log.debug("Reading value {} from field {}", value, field.getField());
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error while reading field " + field.getField(), e);
            }
        }
    }

    /**
     * @throws JGivenMissingRequiredScenarioStateException in case a field requires
     *                                                     a value and the value is not present
     */
    @SuppressWarnings("unchecked")
    public void updateValues(Object object) {
        validateFields(object);
        for (ScenarioStateField field : getScenarioFields(object)) {
            Object value = getValue(field);

            if (value != null) {
                try {
                    field.getField().set(object, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Error while updating field " + field.getField(), e);
                }

                log.debug("Setting field {} to value {}", field.getField(), value);
            } else if (field.isRequired()) {
                throw new JGivenMissingRequiredScenarioStateException(field.getField());
            }
        }
    }

    public <T> void injectValueByType(Class<T> clazz, T value) {
        state.updateValueByType(clazz, value);
    }

    public <T> void injectValueByName(String name, T value) {
        state.updateValueByName(name, value);
    }

    private void updateValue(ScenarioStateField field, Object value) {
        if (field.getResolution() == Resolution.NAME) {
            state.updateValueByName(field.getField().getName(), value);
        } else {
            state.updateValueByType(field.getField().getType(), value);
        }
    }

    private Object getValue(ScenarioStateField field) {
        if (field.getResolution() == Resolution.NAME) {
            return state.getValueByName(field.getField().getName());
        }

        return state.getValueByType(field.getField().getType());
    }

    private void checkGuaranteedStatesAreInitialized(Object instance)
                                        throws JGivenMissingGuaranteedScenarioStateException {
        for (Field field: FieldCache.get(instance.getClass())
                .getFieldsWithAnnotation(ProvidedScenarioState.class, ScenarioState.class)) {
            if (field.isAnnotationPresent(ProvidedScenarioState.class)) {
                if (field.getAnnotation(ProvidedScenarioState.class).guaranteed()) {
                    checkInitialized(instance, field);
                }
            }
            if (field.isAnnotationPresent(ScenarioState.class)) {
                if (field.getAnnotation(ScenarioState.class).guaranteed()) {
                    checkInitialized(instance, field);
                }
            }
        }
    }

    private void checkInitialized(Object instance, Field field) {
        Object value = null;
        try {
            value = field.get(instance);
        } catch (IllegalAccessException e) { }
        if (value == null) {
            throw new JGivenMissingGuaranteedScenarioStateException(field);
        }
    }
}
