package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.Tag;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class HierarchyCalculator {
    private HierarchyCalculator() {
    }

    static Map<String, Map<String, List<String>>> computeGroupedTag(final Map<String, Tag> allTags,
            final Map<String, List<String>> taggedScenarioFiles) {
        return taggedScenarioFiles.entrySet().stream()
                .filter(entry -> allTags.get(entry.getKey()).getShownInNavigation())
                .collect(groupingBy(entry -> fullType(entry, allTags), Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private static String fullType(final Map.Entry<String, List<String>> entry, Map<String, Tag> allTags) {
        final var tag = allTags.get(entry.getKey());

        return tag.getFullType();

    }
}
