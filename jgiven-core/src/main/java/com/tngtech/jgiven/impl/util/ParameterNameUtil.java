package com.tngtech.jgiven.impl.util;

import static java.lang.String.format;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;
import com.tngtech.jgiven.report.model.NamedArgument;

public class ParameterNameUtil {
    private static final Logger log = LoggerFactory.getLogger( ParameterNameUtil.class );

    private static final Paranamer PARANAMER = new BytecodeReadingParanamer();

    private static final Method GET_PARAMETERS_METHOD = getParametersMethod();
    private static Method getNameMethod;

    private static Method getParametersMethod() {
        try {
            /**
             * This method only exist since Java 8, thus we have to use reflection to use it, 
             * to stay compatible with Java 6
             */
            return Method.class.getMethod( "getParameters" );
        } catch( NoSuchMethodException e ) {
            return null;
        }
    }

    /** 
     * @throws NullPointerException iif {@code constructorOrMethod} is {@code null} 
     */
    public static List<NamedArgument> mapArgumentsWithParameterNames( AccessibleObject constructorOrMethod, List<Object> arguments ) {
        Preconditions.checkNotNull( constructorOrMethod, "constructorOrMethod must not be null." );
        Preconditions.checkNotNull( arguments, "arguments must not be null" );

        if( arguments.isEmpty() ) {
            return Collections.emptyList();
        }

        List<String> names = getParameterNames( constructorOrMethod );

        List<NamedArgument> result = Lists.newArrayList();
        if( names.size() == arguments.size() ) {
            for( int idx = 0; idx < names.size(); idx++ ) {
                result.add( new NamedArgument( names.get( idx ), arguments.get( idx ) ) );
            }
        } else {
            log.warn( format( "Different number of retrieved argument names and values: %s.length != %s.length",
                names, arguments ) );
            for( int idx = 0; idx < arguments.size(); idx++ ) {
                result.add( new NamedArgument( "arg" + idx, arguments.get( idx ) ) );
            }
        }
        return result;
    }

    private static List<String> getParameterNames( AccessibleObject constructorOrMethod ) {
        if( GET_PARAMETERS_METHOD != null ) {
            return getParameterNamesUsingJava8( constructorOrMethod );
        } else {
            return getParameterNamesUsingParanamer( constructorOrMethod );
        }
    }

    private static List<String> getParameterNamesUsingJava8( AccessibleObject constructorOrMethod ) {
        try {
            Object[] parameters = (Object[]) GET_PARAMETERS_METHOD.invoke( constructorOrMethod );
            List<String> parameterNames = Lists.newArrayList();
            for( Object parameter : parameters ) {
                String name = (String) getNameMethod( parameter.getClass() ).invoke( parameter );
                parameterNames.add( name );
            }
            return parameterNames;
        } catch( Exception e ) {
            log.warn( format( "Could not call method getParameters on '%s'", constructorOrMethod ), e );
            return Collections.emptyList();
        }
    }

    private static Method getNameMethod( Class<?> aClass ) throws NoSuchMethodException {
        if( getNameMethod == null ) {
            getNameMethod = aClass.getMethod( "getName" );
        }
        return getNameMethod;
    }

    private static List<String> getParameterNamesUsingParanamer( AccessibleObject constructorOrMethod ) {
        try {
            return Arrays.asList( PARANAMER.lookupParameterNames( constructorOrMethod ) );

        } catch( Exception e ) {
            log.warn( format( "Could not determine parameter names of constructor or method '%s'. "
                    + "You should compile your source code with debug information.", constructorOrMethod ), e );
        }
        return Collections.emptyList();
    }

}
