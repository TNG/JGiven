package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.Tag;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class TagMapperTest {
    @Test
    public void map_simple_tag() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Feature");
        tag.setType("Feature");

        // when
        final String snippet = TagMapper.mapTag(tag);

        // then
        Assertions.assertThat(snippet).isEqualTo("[.jg-tag-Feature]#Feature#");
    }

    @Test
    public void map_simple_tag_with_name() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Priority", "Core Features", null);
        tag.setType("FeatureCore");

        // when
        final String snippet = TagMapper.mapTag(tag);

        // then
        Assertions.assertThat(snippet).isEqualTo("[.jg-tag-FeatureCore]#Core Features#");
    }

    @Test
    public void map_simple_tag_with_single_value() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Story", "ACME-1337");
        tag.setType("Story");

        // when
        final String snippet = TagMapper.mapTag(tag);

        // then
        Assertions.assertThat(snippet).isEqualTo("[.jg-tag-Story]#ACME-1337#");
    }

    @Test
    public void map_combined_tag_with_single_value() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Issue", "#1337");
        tag.setType("Issue");
        tag.setPrependType(true);

        // when
        final String snippet = TagMapper.mapTag(tag);

        // then
        Assertions.assertThat(snippet).isEqualTo("[.jg-tag-Issue]#Issue-#1337#");
    }

    @Test
    public void map_simple_tag_with_multiple_values() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Story", List.of("ACME-1337", "ACME-4221"));
        tag.setType("Story");

        // when
        final String snippet = TagMapper.mapTag(tag);

        // then
        Assertions.assertThat(snippet).isEqualTo("[.jg-tag-Story]#ACME-1337, ACME-4221#");
    }

    @Test
    public void map_simple_tag_with_css_class() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Priority", "1");
        tag.setType("Priority");
        tag.setCssClass("hidden");

        // when
        final String snippet = TagMapper.mapTag(tag);

        // then
        Assertions.assertThat(snippet).isEqualTo("[.hidden]#1#");
    }
}
