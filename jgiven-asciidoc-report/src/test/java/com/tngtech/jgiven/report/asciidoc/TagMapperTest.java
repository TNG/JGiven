package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.Tag;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TagMapperTest {
    @Test
    void simple_tag_to_label() {
        // given
        final var tag = new Tag("com.tngtech.jgiven.tags.Feature");
        tag.setType("Feature");

        // when
        final var snippet = TagMapper.toHumanReadableLabel(tag);

        // then
        assertThat(snippet).isEqualTo("[.jg-tag-Feature]#Feature#");
    }

    @Test
    void single_value_tag_to_label() {
        // given
        final var tag = new Tag("com.tngtech.jgiven.tags.Story", "ACME-1337");
        tag.setType("Story");

        // when
        final var snippet = TagMapper.toHumanReadableLabel(tag);

        // then
        assertThat(snippet).isEqualTo("[.jg-tag-Story]#ACME-1337#");
    }

    @Test
    void single_value_tag_with_type_to_label() {
        // given
        final var tag = new Tag("com.tngtech.jgiven.tags.Issue", "#1337");
        tag.setType("Issue");
        tag.setPrependType(true);

        // when
        final var snippet = TagMapper.toHumanReadableLabel(tag);

        // then
        assertThat(snippet).isEqualTo("[.jg-tag-Issue]#Issue-#1337#");
    }

    @Test
    void multiple_value_tag_to_label() {
        // given
        final var tag = new Tag("com.tngtech.jgiven.tags.Story", List.of("ACME-1337", "ACME-4221"));
        tag.setType("Story");

        // when
        final var snippet = TagMapper.toHumanReadableLabel(tag);

        // then
        assertThat(snippet).isEqualTo("[.jg-tag-Story]#ACME-1337, ACME-4221#");
    }

    @Test
    void tag_with_css_class_to_label() {
        // given
        final var tag = new Tag("com.tngtech.jgiven.tags.Priority", "1");
        tag.setType("Priority");
        tag.setCssClass("hidden");

        // when
        final var snippet = TagMapper.toHumanReadableLabel(tag);

        // then
        assertThat(snippet).isEqualTo("[.hidden]#1#");
    }

    @Test
    void tag_with_name_to_label() {
        // given
        final var tag = new Tag("com.tngtech.jgiven.tags.FeatureCore", "Core Features", null);
        tag.setType("FeatureCore");

        // when
        final var snippet = TagMapper.toHumanReadableLabel(tag);

        // then
        assertThat(snippet).isEqualTo("[.jg-tag-FeatureCore]#Core Features#");
    }

    @Test
    void simple_tag_to_AsciiDoc_tag() {
        // given
        final var tag = new Tag("com.tngtech.jgiven.tags.Feature");
        tag.setType("Feature");

        // when
        final var startSnippet = TagMapper.toAsciiDocStartTag(tag);
        final var endSnippet = TagMapper.toAsciiDocEndTag(tag);

        // then
        assertThat(startSnippet).isEqualTo("// tag::tag-com.tngtech.jgiven.tags.Feature[]");
        assertThat(endSnippet).isEqualTo("// end::tag-com.tngtech.jgiven.tags.Feature[]");
    }

    @Test
    void single_value_tag_to_AsciiDoc_tag() {
        // given
        final var tag = new Tag("com.tngtech.jgiven.tags.Feature", "AsciiDoc");
        tag.setType("Feature");

        // when
        final var startSnippet = TagMapper.toAsciiDocStartTag(tag);
        final var endSnippet = TagMapper.toAsciiDocEndTag(tag);

        // then
        assertThat(startSnippet).isEqualTo("// tag::tag-com.tngtech.jgiven.tags.Feature-AsciiDoc[]");
        assertThat(endSnippet).isEqualTo("// end::tag-com.tngtech.jgiven.tags.Feature-AsciiDoc[]");
    }

    @Test
    void single_value_tag_with_type_to_AsciiDoc_tag() {
        // given
        final var tag = new Tag("com.tngtech.jgiven.tags.Issue", "#1337");
        tag.setType("Issue");
        tag.setPrependType(true);

        // when
        final var startSnippet = TagMapper.toAsciiDocStartTag(tag);
        final var endSnippet = TagMapper.toAsciiDocEndTag(tag);

        // then
        assertThat(startSnippet).isEqualTo("// tag::tag-com.tngtech.jgiven.tags.Issue-#1337[]");
        assertThat(endSnippet).isEqualTo("// end::tag-com.tngtech.jgiven.tags.Issue-#1337[]");
    }

    @Test
    void multiple_value_tag_to_AsciiDoc_tag() {
        // given
        final var tag = new Tag("com.tngtech.jgiven.tags.Feature", List.of("AsciiDoc", "Markdown"));

        // when
        final var startSnippet = TagMapper.toAsciiDocStartTag(tag);
        final var endSnippet = TagMapper.toAsciiDocEndTag(tag);

        // then
        assertThat(startSnippet).isEqualTo("// tag::tag-com.tngtech.jgiven.tags.Feature-AsciiDoc,_Markdown[]");
        assertThat(endSnippet).isEqualTo("// end::tag-com.tngtech.jgiven.tags.Feature-AsciiDoc,_Markdown[]");
    }
}
