package com.tngtech.jgiven.impl.tag;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ResolvedTagsTest {


    @Test
    public void testResolvedTagsFiltersForDirectTags() {
        ResolvedTags underTest = TestTagGenerator.getEnumeratedResolvedTags(5);
        assertThat(underTest.getDeclaredTags()).extracting(tag -> tag.getFullType().toString())
            .containsExactlyInAnyOrder("tag1", "tag2", "tag3", "tag4", "tag5");
    }

    @Test
    public void testResolvedTagsFiltersForParents() {
        ResolvedTags underTest = TestTagGenerator.getEnumeratedResolvedTags(5);
        assertThat(underTest.getAncestors()).extracting(tag -> tag.getFullType().toString())
            .containsExactlyInAnyOrder("parent1", "parent2", "parent3", "parent4", "parent5");
    }
}
