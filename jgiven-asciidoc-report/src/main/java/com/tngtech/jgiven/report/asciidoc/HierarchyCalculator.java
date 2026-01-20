package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.Tag;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HierarchyCalculator {
    private final Map<String, List<String>> taggedScenarioFiles;
    private final Map<String, Tag> allTags;

    public HierarchyCalculator(final Map<String, Tag> allTags, final Map<String, List<String>> taggedScenarioFiles) {
        this.taggedScenarioFiles = taggedScenarioFiles;
        this.allTags = allTags;
    }

    Map<String, Map<String, List<String>>> computeGroupedTag() {
        return taggedScenarioFiles.entrySet().stream()
                .filter(entry -> allTags.get(entry.getKey()).getShownInNavigation())
                .collect(Collectors.groupingBy(this::fullType, Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private String fullType(final Map.Entry<String, List<String>> entry) {
        final Tag tag = allTags.get(entry.getKey());

        return tag.getFullType();

    }
}
