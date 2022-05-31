package com.tngtech.jgiven.impl.tag;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.report.model.Tag;
import org.junit.Test;

public class ResolvedTagsTest {


    @Test
    public void testResolvedTagsFiltersForDirectTags() {
        ResolvedTags underTest = TestTagGenerator.getEnumeratedResolvedTags(5);
        assertThat(underTest.getDeclaredTags()).extracting(Tag::getFullType)
            .containsExactlyInAnyOrder("tag1", "tag2", "tag3", "tag4", "tag5");
    }

    @Test
    public void testResolvedTagsFiltersForParents() {
        ResolvedTags underTest = TestTagGenerator.getEnumeratedResolvedTags(5);
        assertThat(underTest.getAncestors()).extracting(Tag::getFullType)
            .containsExactlyInAnyOrder("parent1", "parent2", "parent3", "parent4", "parent5");
    }
}
