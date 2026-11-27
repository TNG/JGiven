package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.Tag;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static com.tngtech.jgiven.report.asciidoc.FeatureName.feature;
import static com.tngtech.jgiven.report.model.Tag.TagClass.tagClass;
import static com.tngtech.jgiven.report.model.Tag.TagId.id;
import static org.assertj.core.api.Assertions.assertThat;

class HierarchyCalculatorTest {

    @Test
    void single_tag() {
        var result = HierarchyCalculator.computeGroupedTag(
                Map.of(id("tag-id"), new Tag(tagClass("tag type"))),
                Map.of(id("tag-id"), List.of(feature("file"))));
        assertThat(result).isEqualTo(
                Map.of(tagClass("tag type"),
                        Map.of(id("tag-id"), List.of(feature("file")))));
    }

    @Test
    void tag_in_multiple_files() {
        var result = HierarchyCalculator.computeGroupedTag(
                Map.of(id("tag-id"), new Tag(tagClass("tag type"))),
                Map.of(id("tag-id"), List.of(feature("file1"), feature("file2"))));
        assertThat(result).isEqualTo(
                Map.of(tagClass("tag type"),
                        Map.of(id("tag-id"), List.of(feature("file1"), feature("file2")))));
    }

    @Test
    void multiple_tags_in_same_file() {
        var result = HierarchyCalculator.computeGroupedTag(
                Map.of(
                        id("tag-id"), new Tag(tagClass("tag type")),
                        id("other-tag"), new Tag(tagClass("tag type"))),
                Map.of(
                        id("tag-id"), List.of(feature("file")),
                        id("other-tag"), List.of(feature("file"))));
        assertThat(result).isEqualTo(
                Map.of(tagClass("tag type"),
                        Map.of(
                                id("tag-id"), List.of(feature("file")),
                                id("other-tag"), List.of(feature("file")))));
    }

    @Test
    void multiple_tag_types_in_different_files() {
        var result = HierarchyCalculator.computeGroupedTag(
                Map.of(id("tag-id1"), new Tag(tagClass("tag type 1")),
                        id("tag-id2"), new Tag(tagClass("tag type 2"))),
                Map.of(id("tag-id1"), List.of(feature("file1")),
                        id("tag-id2"), List.of(feature("file2"))));
        assertThat(result).isEqualTo(
                Map.of(tagClass("tag type 1"), Map.of(id("tag-id1"), List.of(feature("file1"))),
                        tagClass("tag type 2"), Map.of(id("tag-id2"), List.of(feature("file2")))));
    }

    @Test
    void multiple_tag_types_in_overlapping_files() {
        var result = HierarchyCalculator.computeGroupedTag(
                Map.of(id("tag-id1"), new Tag(tagClass("tag type 1")),
                        id("tag-id2"), new Tag(tagClass("tag type 2"))),
                Map.of(id("tag-id1"), List.of(feature("file1"), feature("file2")),
                        id("tag-id2"), List.of(feature("file2"), feature("file3"))));
        assertThat(result).isEqualTo(
                Map.of(tagClass("tag type 1"), Map.of(id("tag-id1"), List.of(feature("file1"), feature("file2"))),
                        tagClass("tag type 2"), Map.of(id("tag-id2"), List.of(feature("file2"), feature("file3")))));
    }
}
