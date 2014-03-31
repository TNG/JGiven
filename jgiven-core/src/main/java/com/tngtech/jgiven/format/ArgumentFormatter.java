package com.tngtech.jgiven.format;

public interface ArgumentFormatter<T> {
    String format( T arg, String... args );
}
