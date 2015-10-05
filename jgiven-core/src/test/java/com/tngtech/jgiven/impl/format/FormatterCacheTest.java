package com.tngtech.jgiven.impl.format;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import com.tngtech.jgiven.format.DefaultFormatter;
import com.tngtech.jgiven.format.Formatter;

public class FormatterCacheTest {

    static class EmptyFormatter<T> implements Formatter<T> {
        @Override
        public String format( T argumentToFormat, Annotation... annotations ) {
            return null;
        }
    }

    private static final Formatter<Object> aFormatter = new EmptyFormatter<Object>();
    private static final Formatter<Object> anotherFormatter = new EmptyFormatter<Object>();

    @Test
    public void testDefaultFormatter() {
        FormatterCache cache = new FormatterCache();
        assertThat( cache.getFormatter( String.class ) ).isSameAs( DefaultFormatter.INSTANCE );
    }

    @Test
    public void testExactType() {
        FormatterCache cache = new FormatterCache();
        cache.setFormatter( Object.class, aFormatter );
        cache.setFormatter( String.class, anotherFormatter );
        assertThat( cache.getFormatter( String.class ) ).isSameAs( anotherFormatter );
    }

    @Test
    public void testObjectType() {
        FormatterCache cache = new FormatterCache();
        cache.setFormatter( Object.class, aFormatter );
        cache.setFormatter( Long.class, anotherFormatter );
        assertThat( cache.getFormatter( String.class ) ).isSameAs( aFormatter );
    }

    @Test
    public void testInterfaceType() {
        FormatterCache cache = new FormatterCache();
        cache.setFormatter( CharSequence.class, aFormatter );
        assertThat( cache.getFormatter( String.class ) ).isSameAs( aFormatter );
    }

    @Test
    public void testInterfaceBeforeObjectInterface() {
        FormatterCache cache = new FormatterCache();
        cache.setFormatter( Object.class, aFormatter );
        cache.setFormatter( CharSequence.class, anotherFormatter );
        assertThat( cache.getFormatter( String.class ) ).isSameAs( anotherFormatter );
    }

    @Test
    public void testTransitiveInterface() {
        FormatterCache cache = new FormatterCache();
        cache.setFormatter( Collection.class, aFormatter );
        assertThat( cache.getFormatter( ArrayList.class ) ).isSameAs( aFormatter );
    }

}
