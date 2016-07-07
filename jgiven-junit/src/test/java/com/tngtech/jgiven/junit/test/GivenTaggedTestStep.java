package com.tngtech.jgiven.junit.test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.junit.test.GivenTaggedTestStep.StageTag;

@StageTag
public class GivenTaggedTestStep extends GivenTestStep {

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @interface StageTag {}

    public void some_step_method_in_a_tagged_stage() {}

}
