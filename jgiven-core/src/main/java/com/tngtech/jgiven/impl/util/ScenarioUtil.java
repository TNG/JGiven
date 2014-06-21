package com.tngtech.jgiven.impl.util;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.lang.String.format;

import java.lang.reflect.AccessibleObject;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;

public class ScenarioUtil {
    private static final Logger log = LoggerFactory.getLogger( ScenarioUtil.class );

    private static final Paranamer PARANAMER = new BytecodeReadingParanamer();

    public static LinkedHashMap<String, ?> mapArgumentsWithParameterNamesOf( AccessibleObject contructorOrMethod, Object[] arguments ) {
        String[] names = getParameterNames( contructorOrMethod );

        LinkedHashMap<String, Object> result = newLinkedHashMap();
        if( arguments != null ) {
            if( names == null ) {
                for( int idx = 0; idx < arguments.length; idx++ ) {
                    result.put( "arg" + idx, arguments[idx] );
                }
            } else if( names.length == arguments.length ) {
                for( int idx = 0; idx < names.length; idx++ ) {
                    result.put( names[idx], arguments[idx] );
                }
            } else {
                log.warn( format( "Different number of retrieved argument names and values: %s.length != %s.length",
                    Arrays.toString( names ), Arrays.toString( arguments ) ) );
            }
        }
        return result;
    }

    private static String[] getParameterNames( AccessibleObject contructorOrMethod ) {
        try {
            return PARANAMER.lookupParameterNames( contructorOrMethod );

        } catch( ParameterNamesNotFoundException e ) {
            log.warn( format( "Could not determine parameter names of constructor or method '%s'. "
                + "You should compile your source code with debug information.", contructorOrMethod ), e );
        }
        return new String[0];
    }

}
