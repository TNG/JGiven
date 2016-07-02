package com.tngtech.jgiven.examples.tags;

import org.junit.Test;

import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.tags.Issue;

/**
 * This example shows how tags can be added dynamically at runtime.
 */
public class DynamicTags extends SimpleScenarioTest<DynamicTags.Steps> {

    @Test
    public void tags_can_be_added_dynamically() {
        getScenario().addTag( Issue.class, "Value" );
    }

    public static class Steps {}

}
