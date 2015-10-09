package com.tngtech.jgiven.impl.format;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.tngtech.jgiven.format.DefaultFormatter;
import com.tngtech.jgiven.format.Formatter;

public class FormatterCache {
    private final Map<Class<?>, Formatter<?>> configuredFormatter = Maps.newHashMap();
    private final LoadingCache<Class<?>, Formatter<?>> cache = CacheBuilder.newBuilder().build( new CacheLoader<Class<?>, Formatter<?>>() {
        @Override
        public Formatter<?> load( Class<?> typeToBeFormatted ) throws Exception {
            if( configuredFormatter.containsKey( typeToBeFormatted ) ) {
                return configuredFormatter.get( typeToBeFormatted );
            }

            if( typeToBeFormatted == Object.class ) {
                return DefaultFormatter.INSTANCE;
            }

            Class<?> superClass = typeToBeFormatted;
            while( ( superClass = superClass.getSuperclass() ) != null ) {
                if( superClass == Object.class ) {
                    break;
                }
                Formatter<?> f = cache.get( superClass );
                if( f != cache.get( Object.class ) ) {
                    return f;
                }
            }

            return getFormatter( typeToBeFormatted, typeToBeFormatted.getInterfaces() );
        }

        private Formatter<?> getFormatter( Class<?> typeToBeFormatted, Class<?>[] interfaces ) throws ExecutionException {
            Formatter<?> objectFormatter = cache.get( Object.class );
            for( Class<?> i : interfaces ) {
                Formatter<?> f = cache.get( i );
                if( f != objectFormatter ) {
                    return f;
                }
            }

            for( Class<?> i : interfaces ) {
                Formatter<?> f = getFormatter( typeToBeFormatted, i.getInterfaces() );
                if( f != objectFormatter ) {
                    return f;
                }
            }
            return objectFormatter;
        }

    } );

    public <T> void setFormatter( Class<T> typeToBeFormatted, Formatter<? super T> formatter ) {
        this.configuredFormatter.put( typeToBeFormatted, formatter );
    }

    public <T> Formatter<? super T> getFormatter( final Class<T> typeToBeFormatted ) {
        if( configuredFormatter.isEmpty() ) {
            return DefaultFormatter.INSTANCE;
        }

        try {
            return (Formatter<? super T>) cache.get( typeToBeFormatted );
        } catch( ExecutionException e ) {
            throw Throwables.propagate( e.getCause() );
        }
    }
}
