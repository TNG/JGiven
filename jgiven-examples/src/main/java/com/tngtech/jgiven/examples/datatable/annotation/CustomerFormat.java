package com.tngtech.jgiven.examples.datatable.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.NamedFormat;
import com.tngtech.jgiven.annotation.NamedFormats;
import com.tngtech.jgiven.examples.datatable.format.ToUpperCaseFormatter;

//@formatter:off
@NamedFormats({
    @NamedFormat( name = "name", format = @Format( value = ToUpperCaseFormatter.class ) ),
    @NamedFormat( name = "email", formatAnnotation = QuotedCustomFormatAnnotationChain.class ),
    @NamedFormat( name = "shippingAddress", formatAnnotation = AddressReducedPOJOFormat.class ),
})
//@formatter:on
@Retention( RetentionPolicy.RUNTIME )
public @interface CustomerFormat {}
