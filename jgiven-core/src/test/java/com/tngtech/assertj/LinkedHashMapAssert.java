package com.tngtech.assertj;

import static com.google.common.collect.Maps.immutableEntry;
import static org.assertj.core.error.ShouldContainExactly.shouldContainExactly;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.assertj.core.api.MapAssert;
import org.assertj.core.data.MapEntry;
import org.assertj.core.internal.Failures;
import org.assertj.core.internal.Maps;
import org.assertj.core.internal.Objects;

public class LinkedHashMapAssert<K, V> extends MapAssert<K, V> {
    private final Objects objects = Objects.instance();
    private final Maps maps = Maps.instance();
    private final Failures failures = Failures.instance();

    protected LinkedHashMapAssert( LinkedHashMap<K, V> actual ) {
        super( actual );
    }

    public LinkedHashMapAssert<K, V> containsExactly( MapEntry... entries ) {
        if( entries == null ) {
            throw new NullPointerException( "The array of entries to look for should not be null" );
        }
        objects.assertNotNull( info, actual );

        // if both actual and values are empty, then assertion passes.
        if( actual.isEmpty() && entries.length == 0 ) {
            return this;
        }
        maps.assertHasSameSizeAs( info, actual, entries );

        int idx = 0;
        for( Iterator<Entry<K, V>> actualIterator = actual.entrySet().iterator(); actualIterator.hasNext(); ) {
            Entry<K, V> entry = actualIterator.next();
            Entry<?, ?> expected = mapEntryOf( entries[idx] );

            if( !entry.equals( expected ) ) {
                throw failures.failure( info, shouldContainExactly( entry, expected, idx ) );
            }
            idx++;
        }
        return this;
    }

    private Entry<?, ?> mapEntryOf( MapEntry mapEntry ) {
        return immutableEntry( mapEntry.key, mapEntry.value );
    }
}