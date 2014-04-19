package com.tngtech.jgiven.format;

import java.util.Arrays;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

/**
 * A default formatter that merely use {@link String#valueOf(Object)}, 
 * except for arrays where {@link Arrays#deepToString(Object[])} is used.
 * @param <T> 
 */
public class DefaultFormatter<T> implements ArgumentFormatter<T> {

    @Override
    public String format( T argumentToFormat, final String... formatterArguments ) {
        if( argumentToFormat == null ) {
            return "null";
        }

        if( argumentToFormat.getClass().isArray() ) {
            return Joiner.on( ", " ).join(
                Iterables.transform( Arrays.asList( (Object[]) argumentToFormat ), new Function<Object, String>() {
                    @Override
                    public String apply( Object input ) {
                        return new DefaultFormatter<Object>().format( input, formatterArguments );
                    }

                } ) );
        }

        return String.valueOf( argumentToFormat );
    }
}
