package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.Tag;
import com.tngtech.jgiven.report.model.Tag.TagId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class HierarchyCalculator {
    private HierarchyCalculator() {
    }

    static Map<String, Map<TagId, List<String>>> computeGroupedTag(final Map<TagId, Tag> allTags,
            final Map<TagId, List<String>> taggedScenarioFiles) {
        return taggedScenarioFiles.entrySet().stream()
                .filter(entry -> allTags.get(entry.getKey()).getShownInNavigation())
                .collect(groupingBy(entry -> fullType(entry.getKey(), allTags), Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private static String fullType(final TagId tagId, Map<TagId, Tag> allTags) {
        return allTags.get(tagId).getFullType();

    }
}
