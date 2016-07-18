package com.tngtech.jgiven.impl.params;

import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.AsProvider;
import com.tngtech.jgiven.config.AbstractJGivenConfiguration;
import com.tngtech.jgiven.config.ConfigurationUtil;
import com.tngtech.jgiven.impl.util.WordUtil;

import java.lang.reflect.Method;

/**
 * The default provider for a stage method, scenario or scenario class.
 *
 * @since 0.12.0
 */
public class DefaultAsProvider implements AsProvider {

    /**
     * Returns the value of the {@link As} annotation if a value was specified.
     * Otherwise, this transforms the method name into a readable sentence and returns it.
     */
    @Override
    public String as( As annotation, Method method ) {
        if( annotation != null && annotationHasExplicitValue( annotation ) ) {
            return annotation.value();
        }

        return methodNameToReadableText( method.getName() );
    }

    String methodNameToReadableText( String methodName ) {
        if( methodName.contains( "_" ) ) {
            return WordUtil.fromSnakeCase( methodName );
        }

        if (WordUtil.isAllUpperCase(methodName)) {
            return methodName;
        }

        return WordUtil.camelCaseToReadableText( methodName );
    }

    /**
     * Returns the value of the {@link As} annotation.
     * Otherwise, this transforms the class name into a readable sentence and returns it.
     */
    @Override
    public String as( As annotation, Class<?> scenarioClass ) {
        if( annotation != null && annotationHasExplicitValue( annotation ) ) {
            return annotation.value();
        }

        AbstractJGivenConfiguration configuration = ConfigurationUtil.getConfiguration( scenarioClass );
        String regEx = configuration.getTestClassSuffixRegEx();
        String classNameWithoutSuffix = scenarioClass.getSimpleName().replaceAll( regEx + "$", "" );

        return WordUtil.splitCamelCaseToReadableText( classNameWithoutSuffix );
    }

    private boolean annotationHasExplicitValue( As annotation ) {
        return !As.NO_VALUE.equals( annotation.value() );
    }

}
