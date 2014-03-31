package com.tngtech.jgiven.impl.util;

import java.io.File;

import com.google.common.base.Predicate;

public class FilePredicates {

    public static Predicate<? super File> endsWith( final String suffix ) {
        return new Predicate<File>() {
            @Override
            public boolean apply( File input ) {
                return input.getName().endsWith( suffix );
            }
        };
    }

}
