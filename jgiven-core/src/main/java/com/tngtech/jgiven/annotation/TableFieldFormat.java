package com.tngtech.jgiven.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Allow to specify a {@link Format} for a named field of a table.<br>
 * 
 * @See {@link Table}
 * @See {@link TableFieldsFormats}
 * 
 * @author dgrandemange
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
public @interface TableFieldFormat {
	/**
	 * @return field name
	 */
	String value();

	/**
	 * @return field {@link Format}
	 */
	Format format();
}
