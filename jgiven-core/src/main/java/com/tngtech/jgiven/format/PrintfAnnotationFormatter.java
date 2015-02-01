package com.tngtech.jgiven.format;

import com.tngtech.jgiven.annotation.Formatf;

/**
 * {@link com.tngtech.jgiven.format.AnnotationArgumentFormatter} that is used by the {@link Formatf}
 * annotation
 */
public class PrintfAnnotationFormatter implements AnnotationArgumentFormatter<Formatf> {

    @Override
    public String format( Object argumentToFormat, Formatf annotation ) {
        return String.format( annotation.value(), argumentToFormat );
    }
}
