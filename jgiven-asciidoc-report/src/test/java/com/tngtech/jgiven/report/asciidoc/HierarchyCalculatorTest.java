package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.Tag;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HierarchyCalculatorTest {

    @Test
    void single_tag() {
        var result = HierarchyCalculator.computeGroupedTag(
                Map.of("tag-id", new Tag("tag type")),
                Map.of("tag-id", List.of("file")));
        assertThat(result).isEqualTo(
                Map.of("tag type",
                        Map.of("tag-id", List.of("file"))));
    }

    @Test
    void tag_in_multiple_files() {
        var result = HierarchyCalculator.computeGroupedTag(
                Map.of("tag-id", new Tag("tag type")),
                Map.of("tag-id", List.of("file1", "file2")));
        assertThat(result).isEqualTo(
                Map.of("tag type",
                        Map.of("tag-id", List.of("file1", "file2"))));
    }

    @Test
    void multiple_tags_in_same_file() {
        var result = HierarchyCalculator.computeGroupedTag(
                Map.of(
                        "tag-id", new Tag("tag type"),
                        "other-tag", new Tag("tag type")),
                Map.of(
                        "tag-id", List.of("file"),
                        "other-tag", List.of("file")));
        assertThat(result).isEqualTo(
                Map.of("tag type",
                        Map.of(
                                "tag-id", List.of("file"),
                                "other-tag", List.of("file"))));
    }

    @Test
    void multiple_tag_types_in_different_files() {
        var result = HierarchyCalculator.computeGroupedTag(
                Map.of("tag-id1", new Tag("tag type 1"),
                        "tag-id2", new Tag("tag type 2")),
                Map.of("tag-id1", List.of("file1"),
                        "tag-id2", List.of("file2")));
        assertThat(result).isEqualTo(
                Map.of("tag type 1", Map.of("tag-id1", List.of("file1")),
                        "tag type 2", Map.of("tag-id2", List.of("file2"))));
    }

    @Test
    void multiple_tag_types_in_overlapping_files() {
        var result = HierarchyCalculator.computeGroupedTag(
                Map.of("tag-id1", new Tag("tag type 1"),
                        "tag-id2", new Tag("tag type 2")),
                Map.of("tag-id1", List.of("file1", "file2"),
                        "tag-id2", List.of("file2", "file3")));
        assertThat(result).isEqualTo(
                Map.of("tag type 1", Map.of("tag-id1", List.of("file1", "file2")),
                        "tag type 2", Map.of("tag-id2", List.of("file2", "file3"))));
    }
}
