package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.report.model.Tag;

final class TagMapper {

    private TagMapper() {
        // static helper class isn't intended to be instantiated
    }

    static String toHumanReadableLabel(final Tag tag) {
        final String cssClassOrType = tag.getCssClass() == null ? "jg-tag-" + tag.getType() : tag.getCssClass();
        return "[." + cssClassOrType + "]#" + tag + "#";
    }

    static String toAsciiDocStartTag(final Tag tag) {
        return "// tag::" + toAsciiDocTagName(tag) + "[]";
    }

    static String toAsciiDocEndTag(final Tag tag) {
        return "// end::" + toAsciiDocTagName(tag) + "[]";
    }

    private static String toAsciiDocTagName(final Tag tag) {
        return tag.toIdString();
    }
}
