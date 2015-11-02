package com.tngtech.jgiven.examples.links;

import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.impl.tag.GoToTestHrefGenerator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IsTag(hrefGenerator = GoToTestHrefGenerator.class)
@Retention( RetentionPolicy.RUNTIME )
public @interface LinkToTest {
    Class<? extends ScenarioTestBase>[] value();
}