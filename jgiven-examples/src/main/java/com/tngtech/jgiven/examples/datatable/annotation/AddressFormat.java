package com.tngtech.jgiven.examples.datatable.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.NamedFormat;
import com.tngtech.jgiven.annotation.NamedFormats;
import com.tngtech.jgiven.examples.datatable.format.ToUpperCaseFormatter;

//@formatter:off
@NamedFormats({
    @NamedFormat( name = "street"),
    @NamedFormat( name = "city", format = @Format( value = ToUpperCaseFormatter.class )),
    @NamedFormat( name = "zipCode"),
    @NamedFormat( name = "state", format = @Format( value = ToUpperCaseFormatter.class )),
    @NamedFormat( name = "country", format = @Format( value = ToUpperCaseFormatter.class )),
})
//@formatter:on
@Retention( RetentionPolicy.RUNTIME )
public @interface AddressFormat {}
