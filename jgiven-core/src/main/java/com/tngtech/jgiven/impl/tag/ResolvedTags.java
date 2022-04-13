package com.tngtech.jgiven.impl.tag;

import com.tngtech.jgiven.report.model.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Container for styled tags declared on a scenario, including the necessary parents to display them.
 */
public class ResolvedTags {
    List<ResolvedTag> resolvedTags = new ArrayList<>();

    public List<Tag> getDeclaredTags() {
        return resolvedTags.stream().map(resolvedTag -> resolvedTag.tag).collect(Collectors.toList());
    }

    public List<Tag> getParents() {
        return resolvedTags.stream().flatMap(resolvedTag -> resolvedTag.parents.stream()).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return resolvedTags.isEmpty();
    }

    /**
     * A single tag declared for a scenario and the parents necessary to display it.
     */
    public static class ResolvedTag {
        public final Tag tag;
        public final List<Tag> parents;

        ResolvedTag(Tag tag, List<Tag> parents) {

            this.tag = tag;
            this.parents = parents;
        }
    }

    static ResolvedTags from(ResolvedTag resolvedTag) {
        ResolvedTags wrapper = new ResolvedTags();
        wrapper.resolvedTags.add(resolvedTag);
        return wrapper;
    }
}
