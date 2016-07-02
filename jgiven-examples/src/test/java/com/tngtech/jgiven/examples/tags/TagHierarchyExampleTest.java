package com.tngtech.jgiven.examples.tags;

import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;

/**
 * This example shows how hierarchical tags work.
 * <p>
 * Hierarchical tags can be created by just annotating a tag annotation with another tag.
 * The other tag becomes a parent tag or category tag. This makes it possible to structure
 * tags hierarchically. It is even possible to form multiple hierarchies/categories so that a
 * tag is contained multiple categories.
 * </p>
 */
public class TagHierarchyExampleTest extends SimpleScenarioTest<TagHierarchyExampleTest.Steps> {

    @ExampleSubCategory
    @Test
    public void tags_can_form_a_hierarchy() {
        given().tags_annotated_with_tags();
        when().the_report_is_generated();
        then().the_tags_appear_in_a_hierarchy();
    }

    @TagThatIsNotVisibleInNavigation
    @AnotherExampleSubCategory
    @Test
    public void parent_tags_can_have_values() {
        given().tags_annotated_with_tags_that_have_values();
        when().the_report_is_generated();
        then().the_tags_appear_in_a_hierarchy();
    }

    public static class Steps {
        void tags_annotated_with_tags() {
        }

        void the_report_is_generated() {
        }

        void the_tags_appear_in_a_hierarchy() {
        }

        void tags_annotated_with_tags_that_have_values() {
        }
    }
}
