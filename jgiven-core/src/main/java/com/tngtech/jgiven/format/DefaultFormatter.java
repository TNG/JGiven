package com.tngtech.jgiven.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;

/**
 * A default formatter that merely use {@link String#valueOf(Object)},
 * except for arrays where {@link java.util.Arrays#deepToString(Object[])} is used.
 * @param <T>
 */
public class DefaultFormatter<T> implements ArgumentFormatter<T>, Formatter<T>, ObjectFormatter<T> {
    public static final DefaultFormatter INSTANCE = new DefaultFormatter();

    @Override
    public String format( T argumentToFormat, final String... formatterArguments ) {
        return format( argumentToFormat );
    }

    @Override
    public String format( T argumentToFormat, Annotation... annotations ) {
        return format( argumentToFormat );
    }

    @Override
    public String format( T argumentToFormat ) {
        if( argumentToFormat == null ) {
            return "null";
        }

        Class<? extends Object> clazz = argumentToFormat.getClass();
        if( clazz.isArray() ) {
            DefaultFormatter<Object> defaultFormatter = new DefaultFormatter<Object>();
            StringBuilder sb = new StringBuilder();
            for( int i = 0; i < Array.getLength( argumentToFormat ); i++ ) {
                if( i > 0 ) {
                    sb.append( ", " );
                }
                sb.append( defaultFormatter.format( Array.get( argumentToFormat, i ) ) );
            }
            return sb.toString();
        }

        return String.valueOf( argumentToFormat );
    }

}
