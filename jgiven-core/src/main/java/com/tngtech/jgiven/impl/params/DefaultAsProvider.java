package com.tngtech.jgiven.impl.params;

import java.lang.reflect.Method;

import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.AsProvider;

/**
 * The default provider for a stage method, scenario or scenario class.
 *
 */
public class DefaultAsProvider implements AsProvider {

    /**
     * Returns the value of the {@link As} annotation.
     */
    @Override
    public String as( As annotation, Method method ) {
        return annotation.value();
    }

    /**
     * Returns the value of the {@link As} annotation.
     */
    @Override
    public String as( As annotation, Class<?> scenarioClass ) {
        return annotation.value();
    }

}
