package com.tngtech.jgiven.examples.datatable.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Formatf;
import com.tngtech.jgiven.examples.datatable.format.ToUpperCaseFormatter;

@Formatf( value = "(uppercased by custom format annotation) %s" )
@Format( value = ToUpperCaseFormatter.class )
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.FIELD } )
public @interface UpperCasedCustomFormatAnnotationChain {
}
