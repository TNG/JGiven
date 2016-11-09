package com.tngtech.jgiven.impl.util;

import com.tngtech.jgiven.relocated.guava.base.Predicate;

import java.io.File;

public class FilePredicates {

    public static Predicate<? super File> endsWith(final String suffix ) {
        return new Predicate<File>() {
            @Override
            public boolean apply( File input ) {
                return input.getName().endsWith( suffix );
            }
        };
    }

}
