package com.tngtech.jgiven.annotation;

import java.lang.annotation.*;

@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD } )
public @interface NestedSteps {
}
