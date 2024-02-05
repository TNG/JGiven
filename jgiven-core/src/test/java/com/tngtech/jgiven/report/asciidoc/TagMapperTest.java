package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.report.model.Tag;
import org.junit.Test;

import java.util.List;

public class TagMapperTest {
    @Test
    public void simple_tag_to_label() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Feature");
        tag.setType("Feature");

        // when
        final String snippet = TagMapper.toHumanReadableLabel(tag);

        // then
        assertThat(snippet).isEqualTo("[.jg-tag-Feature]#Feature#");
    }

    @Test
    public void single_value_tag_to_label() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Story", "ACME-1337");
        tag.setType("Story");

        // when
        final String snippet = TagMapper.toHumanReadableLabel(tag);

        // then
        assertThat(snippet).isEqualTo("[.jg-tag-Story]#ACME-1337#");
    }

    @Test
    public void single_value_tag_with_type_to_label() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Issue", "#1337");
        tag.setType("Issue");
        tag.setPrependType(true);

        // when
        final String snippet = TagMapper.toHumanReadableLabel(tag);

        // then
        assertThat(snippet).isEqualTo("[.jg-tag-Issue]#Issue-#1337#");
    }

    @Test
    public void multiple_value_tag_to_label() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Story", List.of("ACME-1337", "ACME-4221"));
        tag.setType("Story");

        // when
        final String snippet = TagMapper.toHumanReadableLabel(tag);

        // then
        assertThat(snippet).isEqualTo("[.jg-tag-Story]#ACME-1337, ACME-4221#");
    }

    @Test
    public void tag_with_css_class_to_label() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Priority", "1");
        tag.setType("Priority");
        tag.setCssClass("hidden");

        // when
        final String snippet = TagMapper.toHumanReadableLabel(tag);

        // then
        assertThat(snippet).isEqualTo("[.hidden]#1#");
    }

    @Test
    public void tag_with_name_to_label() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.FeatureCore", "Core Features", null);
        tag.setType("FeatureCore");

        // when
        final String snippet = TagMapper.toHumanReadableLabel(tag);

        // then
        assertThat(snippet).isEqualTo("[.jg-tag-FeatureCore]#Core Features#");
    }

    @Test
    public void simple_tag_to_AsciiDoc_tag() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Feature");
        tag.setType("Feature");

        // when
        final String startSnippet = TagMapper.toAsciiDocStartTag(tag);
        final String endSnippet = TagMapper.toAsciiDocEndTag(tag);

        // then
        assertThat(startSnippet).isEqualTo("// tag::com.tngtech.jgiven.tags.Feature[]");
        assertThat(endSnippet).isEqualTo("// end::com.tngtech.jgiven.tags.Feature[]");
    }

    @Test
    public void single_value_tag_to_AsciiDoc_tag() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Feature", "AsciiDoc");
        tag.setType("Feature");

        // when
        final String startSnippet = TagMapper.toAsciiDocStartTag(tag);
        final String endSnippet = TagMapper.toAsciiDocEndTag(tag);

        // then
        assertThat(startSnippet).isEqualTo("// tag::com.tngtech.jgiven.tags.Feature-AsciiDoc[]");
        assertThat(endSnippet).isEqualTo("// end::com.tngtech.jgiven.tags.Feature-AsciiDoc[]");
    }

    @Test
    public void single_value_tag_with_type_to_AsciiDoc_tag() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Issue", "#1337");
        tag.setType("Issue");
        tag.setPrependType(true);

        // when
        final String startSnippet = TagMapper.toAsciiDocStartTag(tag);
        final String endSnippet = TagMapper.toAsciiDocEndTag(tag);

        // then
        assertThat(startSnippet).isEqualTo("// tag::com.tngtech.jgiven.tags.Issue-#1337[]");
        assertThat(endSnippet).isEqualTo("// end::com.tngtech.jgiven.tags.Issue-#1337[]");
    }

    @Test
    public void multiple_value_tag_to_AsciiDoc_tag() {
        // given
        final Tag tag = new Tag("com.tngtech.jgiven.tags.Feature", List.of("AsciiDoc", "Markdown"));

        // when
        final String startSnippet = TagMapper.toAsciiDocStartTag(tag);
        final String endSnippet = TagMapper.toAsciiDocEndTag(tag);

        // then
        assertThat(startSnippet).isEqualTo("// tag::com.tngtech.jgiven.tags.Feature-AsciiDoc,_Markdown[]");
        assertThat(endSnippet).isEqualTo("// end::com.tngtech.jgiven.tags.Feature-AsciiDoc,_Markdown[]");
    }
}
