package com.tngtech.assertj;

import java.util.LinkedHashMap;

/**
 * Extension point for assertion methods for further data types which are (currently) not supported in
 * {@link org.assertj.core.api.Assertions}.
 */
public class Assertions extends org.assertj.core.api.Assertions {

    public static <K, V> LinkedHashMapAssert<K, V> assertThat( LinkedHashMap<K, V> actual ) {
        return new LinkedHashMapAssert<K, V>( actual );
    }
}
