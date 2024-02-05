package com.tngtech.jgiven.report.asciidoc;

import com.google.common.base.Strings;
import com.tngtech.jgiven.report.model.Tag;
import java.util.ArrayList;
import java.util.List;

/**
 * Generate snippets for including feature files via AsciiDoc include macro.
 */
final class AsciiDocSnippetGenerator {
    private static final String LINE_BREAK = System.lineSeparator();
    private final String title;
    private final String scenarioQualifier;
    private final int numScenarios;

    AsciiDocSnippetGenerator(
            final String title,
            final String scenarioQualifier,
            final int numScenarios) {
        this.title = title;
        this.scenarioQualifier = scenarioQualifier;
        this.numScenarios = numScenarios;
    }

    List<String> generateIntroSnippet(final String description) {
        final ArrayList<String> result = new ArrayList<>();

        result.add("== " + this.title);

        if (!description.isEmpty()) {
            result.add("+++" + LINE_BREAK + description + LINE_BREAK + "+++");
        }

        if (numScenarios == 0) {
            result.add("There are no " + scenarioQualifier + ". Keep rocking!");
        } else {
            result.add("There are " + numScenarios + " " + scenarioQualifier + ".");
        }

        return result;
    }

    List<String> generateIndexSnippet(final String featurePath, final List<String> featureFiles, final String tags, final int leveloffset) {
        final String tagSelector = Strings.isNullOrEmpty(tags) ? "" : "tag=" + tags;
        final ArrayList<String> result = new ArrayList<>();

        if (!featureFiles.isEmpty()) {
            result.addAll(generateIncludeSnippet("", leveloffset, featurePath, featureFiles, tagSelector));
        }

        return result;
    }

    List<String> generateTagSnippet(final Tag tag, int scenarioCount, final List<String> features, final int leveloffset) {
        final ArrayList<String> result = new ArrayList<>();

        result.add("=== " + tag.toString());

        if (features.isEmpty()) {
            result.add("There are no tagged scenarios. Keep rocking!");
        } else {
            final String intro = "There are " + scenarioCount + " " + scenarioQualifier + ".";
            final String tagSelector = TagMapper.toAsciiDocTagName(tag);
            result.addAll(generateIncludeSnippet(intro, leveloffset, "../features", features, tagSelector));
        }

        return result;
    }

    private List<String> generateIncludeSnippet(
            final String intro,
            final int leveloffset,
            final String featurePath,
            final List<String> featureFiles,
            final String tags) {
        final ArrayList<String> result = new ArrayList<>();

        if (!intro.isBlank()) {
            result.add(intro);
        }

        if (leveloffset > 0) {
            result.add(":leveloffset: +" + leveloffset);
        } else if (leveloffset < 0) {
            result.add(":leveloffset: -" + Math.abs(leveloffset));
        }

        featureFiles.forEach(fileName -> result.add(includeMacroFor(featurePath, fileName, tags)));

        if (leveloffset > 0) {
            result.add(":leveloffset: -" + leveloffset);
        } else if (leveloffset < 0) {
            result.add(":leveloffset: +" + Math.abs(leveloffset));
        }
        return result;
    }

    private static String includeMacroFor(final String featurePath, final String featureName, final String tags) {
        return "include::" + featurePath + "/" + featureName + ".asciidoc[" + tags + "]";
    }
}
