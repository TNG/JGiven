package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.Tag;
import com.tngtech.jgiven.report.model.Tag.TagId;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

public class HierarchyCalculator {
    private HierarchyCalculator() {
    }

    static Map<String, Map<TagId, List<FeatureName>>> computeGroupedTag(final Map<TagId, Tag> allTags,
            final Map<TagId, List<FeatureName>> taggedScenarioFeatures) {
        return taggedScenarioFeatures.entrySet().stream()
                .filter(entry -> allTags.get(entry.getKey()).getShownInNavigation())
                .collect(groupingBy(
                        entry -> allTags.get(entry.getKey()).getFullType(),
                        toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
