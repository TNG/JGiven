package com.tngtech.jgiven.format;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

/**
 * A default formatter that merely use {@link String#valueOf(Object)},
 * except for arrays where {@link java.util.Arrays#deepToString(Object[])} is used.
 */
public class DefaultFormatter<T> implements ArgumentFormatter<T>, Formatter<T>, ObjectFormatter<T> {
    public static final DefaultFormatter INSTANCE = new DefaultFormatter();

    @Override
    @Nonnull
    public String format(@Nullable T argumentToFormat, @Nullable final String... formatterArguments) {
        return format(argumentToFormat);
    }

    @Override
    @Nonnull
    public String format(@Nullable T argumentToFormat, @Nullable Annotation... annotations) {
        return format(argumentToFormat);
    }

    @Override
    @Nonnull
    public String format(@Nullable T argumentToFormat) {
        if ( argumentToFormat == null ) {
            return "null";
        }

        Class<?> clazz = argumentToFormat.getClass();
        if ( clazz.isArray() ) {
            DefaultFormatter<Object> defaultFormatter = new DefaultFormatter<Object>();
            return convertTArray(argumentToFormat)
                    .map(defaultFormatter::format)
                    .collect(Collectors.joining(", "));
        }
        return String.valueOf(argumentToFormat);
    }

    private Stream<Object> convertTArray(T anyArray) {
        Builder<Object> builder = Stream.builder();
        for (int i = 0; i < Array.getLength(anyArray); i++) {
            builder.add(Array.get(anyArray,i));
        }
        return builder.build();
    }

}
