package com.tngtech.jgiven.impl.tag;

import com.tngtech.jgiven.report.model.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Container for styled tags declared on a scenario, including the necessary parents to display them.
 */
public class ResolvedTags {
    List<ResolvedTag> resolvedTags = new ArrayList<>();

    public List<Tag> getDeclaredTags() {
        return resolvedTags.stream().map(resolvedTag -> resolvedTag.tag).collect(Collectors.toList());
    }

    @SuppressWarnings("CheckStyle")
    public Set<Tag> getAncestors() {
        return resolvedTags.stream()
            .flatMap(resolvedTag -> resolvedTag.ancestors.stream())
            .collect(Collectors.toSet());
    }

    public boolean isEmpty() {
        return resolvedTags.isEmpty();
    }

    /**
     * A single tag declared for a scenario and the ancestors necessary to display it.
     */
    public static class ResolvedTag {
        public final Tag tag;
        public final List<Tag> ancestors;

        ResolvedTag(Tag tag, List<Tag> ancestors) {

            this.tag = tag;
            this.ancestors = ancestors;
        }
    }

    static ResolvedTags from(ResolvedTag resolvedTag) {
        ResolvedTags wrapper = new ResolvedTags();
        wrapper.resolvedTags.add(resolvedTag);
        return wrapper;
    }
}
