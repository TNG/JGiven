package com.tngtech.jgiven.impl.util;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.Hidden;

public class AnnotationUtil {
    public static final String ABSENT = "MARKER FOR ABSENT VALUES IN ANNOTATIONS - JGIVEN INTERNAL DO NOT USE!";

    public static boolean isHidden( Annotation[] annotations ) {
        for( Annotation annotation : annotations ) {
            if( annotation instanceof Hidden ) {
                return true;
            }
        }
        return false;
    }

}
