package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.Tag;

final class TagMapper {

    private TagMapper() {
        // static helper class not intended to be instantiated
    }

    static String mapTag(final Tag tag) {
        final String cssClassOrType = tag.getCssClass() == null ? "jg-tag-" + tag.getType() : tag.getCssClass();
        return "[." + cssClassOrType + "]#" + tag.toString() + "#";
    }
}
