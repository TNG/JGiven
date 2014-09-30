package com.tngtech.jgiven.impl.util;

import static java.lang.String.format;

import java.lang.reflect.AccessibleObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;
import com.tngtech.jgiven.report.model.NamedArgument;

public class ScenarioUtil {
    private static final Logger log = LoggerFactory.getLogger( ScenarioUtil.class );

    private static final Paranamer PARANAMER = new BytecodeReadingParanamer();

    /** 
     * @throws NullPointerException iif {@code constructorOrMethod} is {@code null} 
     */
    public static List<NamedArgument> mapArgumentsWithParameterNames( AccessibleObject contructorOrMethod, List<Object> arguments ) {
        Preconditions.checkNotNull( contructorOrMethod, "constructorOrMethod must not be null." );
        Preconditions.checkNotNull( arguments, "arguments must not be null" );

        List<String> names = getParameterNames( contructorOrMethod );

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

    private static List<String> getParameterNames( AccessibleObject contructorOrMethod ) {
        try {
            return Arrays.asList( PARANAMER.lookupParameterNames( contructorOrMethod ) );

        } catch( ParameterNamesNotFoundException e ) {
            log.warn( format( "Could not determine parameter names of constructor or method '%s'. "
                + "You should compile your source code with debug information.", contructorOrMethod ), e );
        }
        return Collections.emptyList();
    }

}
