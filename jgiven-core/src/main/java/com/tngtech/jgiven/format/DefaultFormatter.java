package com.tngtech.jgiven.format;

import java.util.Arrays;

/**
 * A default formatter that merely use {@link String#valueOf(Object)}, 
 * except for arrays where {@link Arrays#deepToString(Object[])} is used.
 * @param <T> 
 */
public class DefaultFormatter<T> implements ArgumentFormatter<T> {

    @Override
    public String format( T argumentToFormat, String... formatterArguments ) {
        if( argumentToFormat == null ) {
            return "null";
        }

        if( argumentToFormat.getClass().isArray() ) {
            return Arrays.deepToString( (Object[]) argumentToFormat );
        }

        return String.valueOf( argumentToFormat );
    }

}
