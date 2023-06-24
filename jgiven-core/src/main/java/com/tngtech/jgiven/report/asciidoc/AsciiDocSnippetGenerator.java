package com.tngtech.jgiven.report.asciidoc;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;

/**
 * Generate snippets for including feature files via AsciiDoc include macro.
 */
final class AsciiDocSnippetGenerator {
    private final String title;
    private final String scenarioQualifier;
    private final List<String> featureFiles;
    private final int numScenarios;
    private final String tagSelector;

    AsciiDocSnippetGenerator(final String title, final String scenarioQualifier,
                             final List<String> featureFiles, final String tags, final int numScenarios) {
        this.title = title;
        this.scenarioQualifier = scenarioQualifier;
        this.featureFiles = featureFiles;
        this.tagSelector = Strings.isNullOrEmpty(tags) ? "" : "tag=scenario-" + tags;
        this.numScenarios = numScenarios;
    }

    List<String> generateIndexSnippet() {
        final ArrayList<String> result = new ArrayList<>();
        result.add("== " + this.title);

        if (featureFiles.isEmpty()) {
            result.add("There are no " + scenarioQualifier + ". Keep rocking!");
        } else {
            final String intro = "There are " + numScenarios + " " + scenarioQualifier + ".";
            result.addAll(generateIndexSnippet(intro, this.tagSelector));
        }

        return result;
    }

    private List<String> generateIndexSnippet(final String intro, final String tags) {
        final ArrayList<String> result = new ArrayList<>();
        result.add(intro);

        if (!Strings.isNullOrEmpty(tags)) {
            result.add(":leveloffset: -1");
        }

        for (final String fileName : featureFiles) {
            result.add(includeMacroFor(fileName, tags));
        }

        if (!Strings.isNullOrEmpty(tags)) {
            result.add(":leveloffset: +1");
        }
        return result;
    }


    private static String includeMacroFor(final String fileName, final String tags) {
        return "include::features/" + fileName + "[" + tags + "]";
    }
}
